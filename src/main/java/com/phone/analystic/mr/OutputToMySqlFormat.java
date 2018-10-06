/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: OutputToMySqlFormat
 * Author:   14751
 * Date:     2018/9/21 22:09
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr;

import com.phone.Util.JdbcUtil;
import com.phone.analystic.modle.StatsBaseDimension;
import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.GlobalConstants;
import com.phone.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈将结果输出到MySQL的自定义输出格式类〉
 *
 * @author 14751
 * @create 2018/9/21 
 * @since 1.0.0
 */
//public class DBOutputFormat<K extends DBWritable, V> extends OutputFormat<K, V>---参考DBOutputFormat
    //这里的key和value必须继承自一个类（代码的健壮性）---这里也就是父类（key---StatsBaseDimension，value---StatsOutPutValue）
public class OutputToMySqlFormat extends OutputFormat<StatsBaseDimension,StatsOutPutValue> {
    private static final Logger logger = Logger.getLogger(OutputToMySqlFormat.class);
    /**
     * 获取输出记录
     * @param taskAttemptContext
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public RecordWriter<StatsBaseDimension, StatsOutPutValue> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Connection conn = JdbcUtil.getConn();
        Configuration conf = taskAttemptContext.getConfiguration();//这里不能随便new Configuration
        IDimension iDimension = new IDimensionImpl();
        return new OutputToMysqlRecordWritter(conf,conn,iDimension);
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {
        //检测输出的空间，因为不需要输出到磁盘中去
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        //这里不需要些路径，所以为null
        return new FileOutputCommitter(null,taskAttemptContext);
    }

    /**
     * 用于封装写出记录到MySQL的信息
     */
    public static class OutputToMysqlRecordWritter extends RecordWriter<StatsBaseDimension,StatsOutPutValue>{
        Configuration conf = null;
        Connection conn = null;
        IDimension iDimension = null;
        //存储kpi-ps --- 当有相同kpi时，直接取集合中取值ps，减少了与数据库连接的次数
        private Map<KpiType,PreparedStatement> map = new HashMap<KpiType,PreparedStatement>();

        //存储kpi-对应的输出sql语句的个数---用于批量处理
        private Map<KpiType,Integer> batch = new HashMap<KpiType,Integer>();

        public OutputToMysqlRecordWritter(Configuration conf, Connection conn, IDimension iDimension) {
            this.conf = conf;
            this.conn = conn;
            this.iDimension = iDimension;
        }

        /**
         * 写---这里是为了输出到MySQL中的接口做准备（IOutputWriter）
         * 关键方法
         * @param key
         * @param value
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void write(StatsBaseDimension key, StatsOutPutValue value) throws IOException, InterruptedException {

                //获取kpi
                KpiType kpi = value.getKpi();
                //在这之前想拿到ps，必须先通过kpi获取相对应的SQL语句
                PreparedStatement ps = null;
            try {
                //获取ps
                if(map.containsKey(kpi)){
                    ps = map.get(kpi);
                }else {
                    ps = conn.prepareStatement(conf.get(kpi.kpiName));
                    map.put(kpi,ps); //将新增加的ps存储到map中
                }

                int count = 1;
                this.batch.put(kpi,count);
                //也可以写++count,这时候就要写到put操作里面(reduce方法执行一次，count++执行一次，直到没有聚合操作了，才会执行close方法)
                count++;

                //为ps赋值准备
                //根据conf里面的name属性---也就是KPI不同---调用不同的接口实现类
                String className = conf.get(GlobalConstants.PREFIX_OUTPUT+ kpi.kpiName);
                //com.phone.analystic.mr.newuser.NewUserOutputWriter
                Class<?> classz = Class.forName(className);//将字符串转换成类---反射
                //返回的是Object类型，需要强转，一种多态的体现，代码的健壮性，也可以用他的实现类取强转
                IOutputWriter writter = (IOutputWriter) classz.newInstance();  //用来调用对象里面的方法
                //调用IOutputWritter中的方法（实现类）--- 快捷键直接跳入接口的实现类---ctrl+Alt+B （技巧）
                writter.output(conf,key,value,ps,iDimension);

                //对赋值号的ps进行执行
                //特殊情况：如果当前类语句不是50的整数倍，怎么办？？？
                //解决方法：剩余的SQL语句在close方法里面执行

                if(batch.size() % 50 == 0){ //是否有50条SQL语句
                    ps.executeBatch(); //批量执行
//                    this.conn.commit(); //提交批处理执行(注意点.这里不需要加,否则会报错)
                    batch.remove(kpi); //将执行完的sql语句个数移除掉
                }
            } catch (Exception e) {
                logger.warn("写数据失败！！！",e);
            }
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            try {
                for(Map.Entry<KpiType,PreparedStatement> en:map.entrySet()){
                    en.getValue().executeBatch();//批处理执行剩余的ps
//                    this.conn.commit();
                }
            } catch (SQLException e) {
                logger.warn("执行剩余的ps失败！！！",e);
            }finally {
                for(Map.Entry<KpiType,PreparedStatement> en:map.entrySet()){
                    JdbcUtil.close(conn,en.getValue(),null);//关闭所有能关闭的资源
                }
            }
        }
    }
}

