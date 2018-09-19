/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ETLToHdfsRunner
 * Author:   14751
 * Date:     2018/9/19 14:59
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.etl.mr;

import com.phone.Util.TimeUtil;
import com.phone.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * 〈一句话功能简述〉<br> 
 * 〈〉 
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 * yarn jar ./   package.classname -d 2018-09-19 提交job到集群中
 *
 * 这里的配置文件conf可以放在resource里面，也可以在程序中用set设置
 *
 * 将第三方jar包全部打包或者在hadoop的类环境变量里面添加 yarn classpath（后者建议使用，jar包较小）
 * 或者加入到Linux中的java jre里面 --- JVM
 *
 * package和install都是打包，只不过install可以将jar包放在你的工程里面
 *
 * plugin是将一些第三方jar包添加到我们打的jar包的源码中
 */
public class ETLToHdfsRunner implements Tool{
    private static final Logger logger = Logger.getLogger(ETLToHdfsRunner.class);
    private Configuration conf = new Configuration();

    //主函数
    public static void main(String[] args){
        try {
            ToolRunner.run(new Configuration(),new ETLToHdfsRunner(),args);
        } catch (Exception e) {
            logger.warn("执行ETL TO HDFS异常！！!",e);
        }
    }

    @Override
    public void setConf(Configuration conf) {
        conf = this.conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        //1.获取-d之后的日期并存储到hdfs中，如果没有-d或者日期不合法则使用昨天为默认值
        this.handleArgs(conf,args);
        //获取job
        Job job = Job.getInstance(conf,"ETL TO HDFS");

        job.setJarByClass(ETLToHdfsRunner.class);

        //设置map相关参数
        job.setMapperClass(EtlToHdfs.MyMapper.class);
        job.setOutputKeyClass(LogWritable.class);
        job.setOutputValueClass(NullWritable.class);

        //没有reduce
        job.setNumReduceTasks(0);

        //设置输入输出
        this.handleInputOutput(job);
        return job.waitForCompletion(true)?1:0;
    }

    /**
     *
     * @param args
     */
    private void handleArgs(Configuration conf,String[] args){
        String date = null;
        if(args.length > 0){
            //循环args
            for(int i=0;i<args.length;i++){
                //判断参数中是否有-d
                if(args[i].equals("-d")){
                    if(i+1 <= args.length){
                        date = args[i+1];
                        break;
                    }
                }
            }

            //判断
            if(StringUtils.isEmpty(date)){
                date = TimeUtil.getYesterday();
            }

            //将date存储到conf中 --- key-value
            conf.set(GlobalConstants.RUNNING_DATE,date);
        }

    }

    /**
     * 设置输入输出
     * @param job
     */
    private void handleInputOutput(Job job) {
       String[] fields = job.getConfiguration().get(GlobalConstants.RUNNING_DATE).split("-");
        String month = fields[1];
        String day = fields[2];

        try {
            FileSystem fs = FileSystem.get(job.getConfiguration());
            Path inpath = new Path("/log/" + month + "/" + day);
            Path outpath = new Path("/ods/" + month + "/"+ day);
            if(fs.exists(inpath)){
                FileInputFormat.addInputPath(job,inpath);
            }else{
                throw new RuntimeException("输入路径不存在inpath" + inpath.toString());
            }

            //判断输出路径是否存在
            if(fs.exists(outpath)){
                fs.delete(outpath,true);
            }

            //设置输出
            FileOutputFormat.setOutputPath(job,outpath);
        } catch (IOException e) {
            logger.warn("设置输入输出路径异常！！！",e);
        }
    }
}