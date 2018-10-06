/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: NewUserRunner
 * Author:   14751
 * Date:     2018/9/21 2:07
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.newuser;

import com.phone.Util.JdbcUtil;
import com.phone.Util.TimeUtil;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.base.DateDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.analystic.mr.OutputToMySqlFormat;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.DateEnum;
import com.phone.common.GlobalConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;


/**
 * 〈一句话功能简述〉<br>
 * 〈运行类---主类〉
 *
 * @author 14751
 * @create 2018/9/21
 * @since 1.0.0
 */
public class NewBrowserUserRunner implements Tool {
    private static final Logger logger = Logger.getLogger(NewBrowserUserRunner.class);
    private Configuration conf = new Configuration();

    //主函数---入口
    public static void main(String[] args){
        try {
            ToolRunner.run(new Configuration(),new NewBrowserUserRunner(),args);
        } catch (Exception e) {
            logger.warn("BROWSER_NEW_USER TO MYSQL is failed !!!",e);
        }
    }

    @Override
    public void setConf(Configuration configuration) {
        conf.addResource("output_mapping.xml");
        conf.addResource("output_writter.xml");
        conf.addResource("total_mapping.xml");
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        //为结果表中的created赋值，设置到conf中,需要我们传递参数
        this.setArgs(args,conf);

        Job job = Job.getInstance(conf,"Browser_NEW_USER TO MYSQL");

        job.setJarByClass(NewBrowserUserRunner.class);

        //设置map相关参数
        job.setMapperClass(NewBrowserUserMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutPutValue.class);

        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(NewUserReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutPutWritable.class);
        job.setOutputFormatClass(OutputToMySqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
//        return job.waitForCompletion(true)? 0:1;
        if(job.waitForCompletion(true)){
            this.computeNewTotalUser(job);//修改1
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 计算新增总用户---浏览器模块
     * @param job
     */
    private void computeNewTotalUser(Job job) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //获取运行当天时间和运行前一天时间
            String date = job.getConfiguration().get(GlobalConstants.RUNNING_DATE);
            long nowDay = TimeUtil.parseString2Long(date);
            long yesterDay = nowDay - GlobalConstants.DAY_OF_MILISECONDS;

            //获取对应的时间维度
            DateDimension nowDateDimension = DateDimension.buildDate(nowDay, DateEnum.DAY);
            DateDimension yesterdayDimension = DateDimension.buildDate(yesterDay, DateEnum.DAY);

            int nowDimensionId = -1;
            int yesterdayDimensionId = -1;

            //获取维度的id
            IDimension iDimension = new IDimensionImpl();
            nowDimensionId = iDimension.getDimensionIdByObject(nowDateDimension);
            yesterdayDimensionId = iDimension.getDimensionIdByObject(yesterdayDimension);

            //判断对应的时间维度id是否大于0
            conn = JdbcUtil.getConn();
            Map<String,Integer> map = new HashMap<String,Integer>();
            if(yesterdayDimensionId > 0){
                ps = conn.prepareStatement(conf.get(GlobalConstants.PREFIX_TOTAL+"browser_new_total_user"));
                //给ps赋值
                ps.setInt(1,yesterdayDimensionId);
                //执行SQL语句
                rs = ps.executeQuery();
                while(rs.next()){
                    int platformId = rs.getInt("platform_dimension_id");
                    int browserId = rs.getInt("browser_dimension_id");
                    int totalNewUser = rs.getInt("total_install_users");
                    //存储
                    map.put(platformId+"-"+browserId+"-",totalNewUser);
                }
            }

            if(nowDimensionId > 0){
                ps = conn.prepareStatement(conf.get(GlobalConstants.PREFIX_TOTAL+"BROWSER_new_user"));
                //给ps赋值
                ps.setInt(1,nowDimensionId);
                //执行SQL语句
                rs = ps.executeQuery();
                while(rs.next()){
                    int platformId = rs.getInt("platform_dimension_id");
                    int browserId = rs.getInt("browser_dimension_id");
                    int newUser = rs.getInt("new_install_users");
                    //存储
                    //如果前一天中有相同的platformId,则将两者相加
                    if(map.containsKey(platformId+"-"+browserId+"-")){
                        newUser += map.get(platformId+"-"+browserId+"-");
                    }
                    //如果没有当天该平台下的总新增用户就是当天新增用户，添加到map中，为后一天计算准备
                    map.put(platformId+"-"+browserId+"-",newUser);
                }
            }

            //更新新增的总用户
            ps = conn.prepareStatement(conf.get(GlobalConstants.PREFIX_TOTAL+"BROWSER_new_update_user"));
            //给ps赋值
            for(Map.Entry<String,Integer> en:map.entrySet()){
                ps.setInt(1,nowDimensionId);
                String[] splited = en.getKey().split("-");
                ps.setInt(2,Integer.parseInt(splited[0]));
                ps.setInt(3,Integer.parseInt(splited[1]));
                ps.setInt(4,en.getValue());
                ps.setString(5,conf.get(GlobalConstants.RUNNING_DATE));
                ps.setInt(6,en.getValue());
                ps.execute();
            }
        } catch (Exception e) {
            logger.warn("运行统计总的新增用户失败---浏览器模块！！！",e);
        }
    }

    /**
     * 参数处理  ,将接收到的日期存储在conf中，以供后续使用
     * @param args  如果没有传递日期，则默认使用昨天的日期
     * @param conf
     */
    private void setArgs(String[] args, Configuration conf) {
        String date = null;
        for (int i = 0;i < args.length;i++){
            if(args[i].equals("-d")){
                if(i+1 < args.length){
                    date = args[i+1];
                    break;
                }
            }
        }
        //代码到这儿，date还是null，默认用昨天的时间
        if(date == null){
            date = TimeUtil.getYesterday();
        }
        //然后将date设置到时间conf中
        conf.set(GlobalConstants.RUNNING_DATE,date);
    }

    /**
     * 设置输入输出,_SUCCESS文件里面是空的，所以可以直接读取清洗后的数据存储目录
     * @param job
     */
    private void handleInputOutput(Job job) {
        String[] fields = job.getConfiguration().get(GlobalConstants.RUNNING_DATE).split("-");
        String month = fields[1];
        String day = fields[2];

        try {
            FileSystem fs = FileSystem.get(job.getConfiguration());
            Path inpath = new Path("/ods/" + month + "/" + day);
            if(fs.exists(inpath)){
                FileInputFormat.addInputPath(job,inpath);
            }else{
                throw new RuntimeException("输入路径不存在inpath" + inpath.toString());
            }
        } catch (IOException e) {
            logger.warn("设置输入输出路径异常！！！",e);
        }
    }
}
