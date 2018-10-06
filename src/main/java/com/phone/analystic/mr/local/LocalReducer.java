/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: LocalReducer
 * Author:   14751
 * Date:     2018/9/27 1:49
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.local;

import com.phone.analystic.modle.StatsLocalDimension;
import com.phone.analystic.modle.value.map.TextOutputValue;
import com.phone.analystic.modle.value.reduce.LocalOutputValue;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉 
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class LocalReducer extends Reducer<StatsLocalDimension,TextOutputValue,StatsLocalDimension,LocalOutputValue>{
   private static final Logger logger = Logger.getLogger(LocalReducer.class);
   private Set<String> unique = new HashSet<String>();//UUID的去重个数
   private Map<String,Integer> map = new HashMap<String,Integer>();//sessionId---对应的个数（同一个sessionId）
    private LocalOutputValue v = new LocalOutputValue();

    @Override
    protected void reduce(StatsLocalDimension key, Iterable<TextOutputValue> values, Context context) throws IOException, InterruptedException {
        //清空uniq
        this.unique.clear();
        this.map.clear();
        //必须清空，否则会出现表面key相同的情况（实际在hashmap中，两个key的hashcode值不一样），这是因为toString方法引起的现象

        //循环map阶段传过来的value
        for(TextOutputValue tv:values){
            //将UUID取出来添加到set中
            if(StringUtils.isNotEmpty(tv.getUuid())){
                this.unique.add(tv.getUuid());
            }

            if(StringUtils.isNotEmpty(tv.getSessionId())){
                if(map.containsKey(tv.getSessionId())){
                    map.put(tv.getSessionId(),2);
                }else {
                    map.put(tv.getSessionId(),1);
                }
            }
        }

        //构建输出的value
        this.v.setActiveUser(this.unique.size());//UUID的去重个数
        this.v.setSessions(this.map.size());//sessionId的去重个数

        int boundSessions = 0;//跳出回话的个数
        //循环
        for(Map.Entry<String,Integer> en:map.entrySet()){
            if(en.getValue() < 2){
                boundSessions ++ ;
            }
        }

        this.v.setBoundSessions(boundSessions);//设置跳出回话的个数
        //设置KPI
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
        context.write(key,this.v);
    }
}