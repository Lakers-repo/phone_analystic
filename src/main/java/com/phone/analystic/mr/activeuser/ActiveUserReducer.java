/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: NewUserReducer
 * Author:   14751
 * Date:     2018/9/20 17:38
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.activeuser;

import com.phone.Util.TimeUtil;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.common.DateEnum;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈ActiveUserReducer---reduce方法〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class ActiveUserReducer extends Reducer<StatsUserDimension,TimeOutPutValue,StatsUserDimension,OutPutWritable> {
    private static final Logger logger = Logger.getLogger(ActiveUserReducer.class);
    private OutPutWritable v = new OutPutWritable();
    private Set unique = new HashSet();//用于去重，利用HashSet
    private MapWritable map = new MapWritable();

    //按小时统计的集合
    private Map<Integer,Set<String>> hourlyMap = new HashMap<Integer, Set<String>>();//存储小时---对应的活跃用户
    private MapWritable hourlyMapWritable = new MapWritable();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //循环初始化
        for(int i=0;i<24;i++){
            hourlyMap.put(i,new HashSet<String>());
            hourlyMapWritable.put(new IntWritable(i),new IntWritable(0));//存储最终的数据（按小时统计），最后设置到v中
        }
    }

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutPutValue> values, Context context) throws IOException, InterruptedException {
        try {
            //判断事件是哪一个
            String kpi = key.getStatsCommonDimension().getKpiDimension().getKpiName();//两种可能，user和browser_user
            if(kpi.equals(KpiType.ACTIVE_USER.kpiName)){
                //循环map阶段传过来的value
                for(TimeOutPutValue tv:values){//这里同时做了两件事情
                    //将uuid取出来添加到set中
                    this.unique.add(tv.getId());
                    int hour = TimeUtil.getDateInfo(tv.getTime(), DateEnum.HOUR);//获取小时点
                    hourlyMap.get(hour).add(tv.getId());
                }
                //构建输出的value---按小时计算的活跃用户
                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
                //循环赋值
                for (Map.Entry<Integer,Set<String>> en:hourlyMap.entrySet()){
                    this.hourlyMapWritable.put(new IntWritable(en.getKey()),
                            new IntWritable(en.getValue().size()));
                }
                this.v.setValue(hourlyMapWritable);
                context.write(key,this.v);  //输出

                //针对以前普通的活跃用户的输出
                //构建输出的value---活跃用户
                this.map.put(new IntWritable(-1),new IntWritable(this.unique.size()));
                this.v.setValue(map);
                //还需要设置kpi
                this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
                context.write(key,this.v);  //输出
            }else {
                //循环
                for (TimeOutPutValue tv : values) {
                    this.unique.add(tv.getId());//将uuid取出添加到set中进行去重操作
                }

                //构造输出的value---浏览器模块下的活跃用户
                this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));

                //通过集合的size统计新增用户uuid的个数，前面的key可以随便设置，就是用来标识新增用户个数的（比较难理解）
                this.map.put(new IntWritable(-1), new IntWritable(this.unique.size()));
                this.v.setValue(this.map);

                context.write(key, this.v);//输出
            }
        }finally {
            //因为setup只执行一次，所以在清空之后，循环初始化
            this.unique.clear();
            this.hourlyMap.clear();
            this.hourlyMapWritable.clear();
            //循环初始化
            for (int i = 0;i<24;i++){
                hourlyMap.put(i,new HashSet<String>());
                hourlyMapWritable.put(new IntWritable(i),new IntWritable(0));
            }
        }

        //第二种方法---map端输出量太大---实际需求应该保证map端输出少，reduce端输出多（重点）
       /* try{
            //判断事件是哪一个的
            String kpi = key.getStatsCommonDimension().getKpiDimension().getKpiName();
            if(kpi.equals(KpiType.HOURLY_ACTIVE_USER.kpiName)){
                //循环map阶段传过来的value
                for (TimeOutputValue tv:values) {
                    int hour = TimeUtil.getDateInfo(tv.getTime(), DateEnum.HOUR);
                    hourlyMap.get(hour).add(tv.getId());
                }
                //构建输出的value
                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
                //循环赋值
                for (Map.Entry<Integer,Set<String>> en:hourlyMap.entrySet()){
                    this.hourlyMapWritable.put(new IntWritable(en.getKey()),
                            new IntWritable(en.getValue().size()));
                }
                this.v.setValue(hourlyMapWritable);
                //输出
                context.write(key,this.v);

            } else {
                //循环map阶段传过来的value
                for (TimeOutputValue tv:values) {
                    //将uuid取出来添加到set中
                    this.unique.add(tv.getId());
                }

                //构建输出的value
                this.map.put(new IntWritable(-1),new IntWritable(this.unique.size()));

                this.v.setValue(map);
                //还需要设置kpi
                this.v.setKpi(KpiType.valueOfType(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
                //输出即可
                context.write(key,this.v);
            }
        } finally {
            this.unique.clear();
            this.hourlyMap.clear();
            this.hourlyMapWritable.clear();
            //循环初始化
            for (int i = 0;i<24;i++){
                hourlyMap.put(i,new HashSet<String>());
                hourlyMapWritable.put(new IntWritable(i),new IntWritable(0));
            }
        }*/
    }
}