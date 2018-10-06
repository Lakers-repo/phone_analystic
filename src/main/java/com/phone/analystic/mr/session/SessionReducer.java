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
package com.phone.analystic.mr.session;

import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.common.GlobalConstants;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈session的reducer---reducer端对于同一个key执行一次reducer方法〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class SessionReducer extends Reducer<StatsUserDimension,TimeOutPutValue,StatsUserDimension,OutPutWritable> {
    private static final Logger logger = Logger.getLogger(SessionReducer.class);
    private OutPutWritable v = new OutPutWritable();
    private MapWritable map = new MapWritable();
    //用于存储sessionId---同一sessionId对应的时间戳集合(map的key是不可以重复的，实现usid去重的功能)
    private Map<String,List<Long>> listMap = new HashMap<String,List<Long>>();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutPutValue> values, Context context) throws IOException, InterruptedException {
        map.clear();//清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value
        listMap.clear();//清空

        for(TimeOutPutValue tv : values){//循环map端输出的value
            String sessionId = tv.getId();
            long sessionTime = tv.getTime();
            if(listMap.containsKey(sessionId)){
                List<Long> list = listMap.get(tv.getId());
                list.add(sessionTime);
                listMap.put(sessionId,list);
            }else{
                List<Long> list = new ArrayList<>();
                list.add(sessionTime);
                listMap.put(sessionId,list);
            }
        }

        //构造输出的value
        //1.session的个数
        map.put(new IntWritable(-1),new IntWritable(this.listMap.size()));

        //2.每个session的时长
        int sessionLength = 0;
        for(Map.Entry<String,List<Long>> entry:listMap.entrySet()){
            //判断session对应的时间戳个数是否大于1
            if(entry.getValue().size() >= 2){
                Collections.sort(entry.getValue());
                sessionLength += (entry.getValue().get(entry.getValue().size()-1)-entry.getValue().get(0));//首尾时间戳相减
                System.out.println(entry.getKey()+"的长度为:"+sessionLength);
            }
        }

        if(sessionLength > 0 && sessionLength <= GlobalConstants.DAY_OF_MILISECONDS){
            if(sessionLength % 1000 == 0){
                sessionLength = sessionLength / 1000;//单位为秒
            }else{
                sessionLength = sessionLength / 1000 + 1;//不足1秒当1秒计算
            }
        }
        this.map.put(new IntWritable(-2),new IntWritable(sessionLength));

        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
        this.v.setValue(this.map);

        context.write(key,this.v);//输出
    }
}