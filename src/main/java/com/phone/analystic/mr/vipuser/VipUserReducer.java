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
package com.phone.analystic.mr.vipuser;

import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈VipUserReducer---reduce方法〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class VipUserReducer extends Reducer<StatsUserDimension,TimeOutPutValue,StatsUserDimension,OutPutWritable> {
    private static final Logger logger = Logger.getLogger(VipUserReducer.class);
    private OutPutWritable v = new OutPutWritable();
//    private Set<String> unique = new HashSet();//用于去重umid，利用HashSet
    private MapWritable map = new MapWritable();
    private Map<String,List<Long>> listMap = new HashMap<String,List<Long>>();//去重umid和对应的时间，以便求后面的时间排序

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutPutValue> values, Context context) throws IOException, InterruptedException {
        //清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value
        map.clear();
        listMap.clear();

        //循环
        for(TimeOutPutValue tv : values){
//            this.unique.add(tv.getId());//将umid取出添加到set中进行去重操作
            if(listMap.containsKey(tv.getId())){
                listMap.get(tv.getId()).add(tv.getTime());
            }else{
                List<Long> list = new ArrayList<Long>();
                list.add(tv.getTime());
                listMap.put(tv.getId(),list);
            }
        }

        //循环listMap（将umid插入数据库---在mapper和memberutil也可以插入）
        for(Map.Entry<String,List<Long>> en: listMap.entrySet()){
            this.v.setKpi(KpiType.MEMBER_INFO);
            this.map.put(new IntWritable(-2),new Text(en.getKey()));
            Collections.sort(en.getValue());//对于每一个新增会员id相应时间戳进行排序
            this.map.put(new IntWritable(-3),new LongWritable(en.getValue().get(0)));//获取最小的时间戳
            this.v.setValue(this.map);//一定要设置
            context.write(key,this.v);
        }

        //构造输出的value
        //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));

        //通过集合的size统计新增用户umid的个数，前面的key可以随便设置，就是用来标识新增用户个数的（比较难理解）
        this.map.put(new IntWritable(-1),new IntWritable(this.listMap.size()));
        this.v.setValue(this.map);
        //输出
        context.write(key,this.v);
    }
}