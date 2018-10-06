/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: NewUserMapper
 * Author:   14751
 * Date:     2018/9/19 21:25
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.local;

import com.phone.analystic.modle.StatsCommonDimension;
import com.phone.analystic.modle.StatsLocalDimension;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.base.*;
import com.phone.analystic.modle.value.map.TextOutputValue;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.common.DateEnum;
import com.phone.common.EventLogContant;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈Mapper 地域模块〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 */
public class LocalMapper extends Mapper<LongWritable,Text,StatsLocalDimension,TextOutputValue> {
    private static final Logger logger = Logger.getLogger(LocalMapper.class);
    private StatsLocalDimension k = new StatsLocalDimension();
    private TextOutputValue v = new TextOutputValue();
    private KPIDimension localKpi = new KPIDimension(KpiType.LOCAL.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if(StringUtils.isEmpty(line)){
            return ;
        }

        //拆分
        String[] fields = line.split("\u0001");
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];
            String uuid = fields[3];
            String sessionId = fields[5];
            String country = fields[28];
            String province = fields[29];
            String city = fields[30];

        //对三个字段进行空判断
        if(StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(platform)){
            logger.warn("uuid&&serverTime&&platform must not null.memberId:"
                    +"  serverTime:"+serverTime+"  platform:"+platform);
            return;
        }

        if(StringUtils.isEmpty(uuid)){
            uuid = "";
        }

        if(StringUtils.isEmpty(sessionId)){
            sessionId = "";
        }

        //构造输出的key
        long stime = Long.valueOf(serverTime);
        PlatformDimension platformDimension = PlatformDimension.getInstnce(platform);
        DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
        LocationDimension locationDimension = LocationDimension.buildLocal(country,province,city);
        StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();
        //为StatsCommonDimension设值
        statsCommonDimension.setDateDimension(dateDimension);
        statsCommonDimension.setPlatformDimension(platformDimension);
        statsCommonDimension.setKpiDimension(localKpi);
        this.k.setLocationDimension(locationDimension);
        this.k.setStatsCommonDimension(statsCommonDimension);

        //构造value
        this.v.setUuid(uuid);
        this.v.setSessionId(sessionId);

        context.write(this.k,this.v);//输出
    }
}