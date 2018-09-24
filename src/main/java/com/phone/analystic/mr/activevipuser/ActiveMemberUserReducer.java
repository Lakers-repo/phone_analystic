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
package com.phone.analystic.mr.activevipuser;

import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈ActiveMemberUserReducer---reduce方法〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class ActiveMemberUserReducer extends Reducer<StatsUserDimension,TimeOutPutValue,StatsUserDimension,OutPutWritable> {
    private static final Logger logger = Logger.getLogger(ActiveMemberUserReducer.class);
    private OutPutWritable v = new OutPutWritable();
    private Set unique = new HashSet();//用于去重，利用HashSet
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutPutValue> values, Context context) throws IOException, InterruptedException {
        //清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value
        map.clear();

        //循环
        for(TimeOutPutValue tv : values){
            this.unique.add(tv.getId());
        }

        //构造输出的value
        //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));

        this.map.put(new IntWritable(-1),new IntWritable(this.unique.size()));
        this.v.setValue(this.map);
        //输出
        context.write(key,this.v);

    }
}