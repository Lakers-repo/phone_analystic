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
package com.phone.analystic.mr.activeuser;

import com.phone.analystic.modle.StatsCommonDimension;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.base.BrowserDimension;
import com.phone.analystic.modle.base.DateDimension;
import com.phone.analystic.modle.base.KPIDimension;
import com.phone.analystic.modle.base.PlatformDimension;
import com.phone.analystic.modle.value.map.TimeOutPutValue;
import com.phone.common.DateEnum;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈ActiveUserMapper---mapper函数 简单的封装〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 * 用户模块下的活跃用户---当天所有事件中去重的UUID个数---也就是说不需要通过事件名称再次去重
 */
public class ActiveUserMapper extends Mapper<LongWritable,Text,StatsUserDimension,TimeOutPutValue> {
    private static final Logger logger = Logger.getLogger(ActiveUserMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutPutValue v = new TimeOutPutValue();

    private KPIDimension activeUserKpi = new KPIDimension(KpiType.ACTIVE_USER.kpiName);
    private KPIDimension activeBrowserUserKpi = new KPIDimension(KpiType.BROWSER_ACTIVE_USER.kpiName);
//    private KPIDimension hourlyUserKpi = new KPIDimension(KpiType.HOURLY_ACTIVE_USER.kpiName);//这样写map端输出量较大

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if(StringUtils.isEmpty(line)){
            return ;
        }

        //拆分
        String[] fields = line.split("\u0001");
        //en是事件名称
        String en = fields[2];
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];
            String uuid = fields[3];
            String browserName = fields[24];
            String browserVersion = fields[25];
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid)) {
                logger.info("serverTime & active_uuid is null serverTime:" + serverTime + ".active_uuid" + uuid);
                return;
            }

            //构造输出的key
            long stime = Long.valueOf(serverTime);
            PlatformDimension platformDimension = PlatformDimension.getInstnce(platform);
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();

            //为StatsCommonDimension设值
            statsCommonDimension.setDateDimension(dateDimension);
            statsCommonDimension.setPlatformDimension(platformDimension);

            //用户模块下的活跃用户
            //设置默认的浏览器对象(因为新增用户指标并不需要浏览器维度，所以赋值为null)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
            statsCommonDimension.setKpiDimension(activeUserKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(uuid);//构建输出的value(并不需要时间time)
            this.v.setTime(stime);
            context.write(this.k, this.v); //输出

//            statsCommonDimension.setKpiDimension(hourlyUserKpi);
//            this.k.setStatsCommonDimension(statsCommonDimension);
//            context.write(this.k,this.v);

            //浏览器模块新增用户
            statsCommonDimension.setKpiDimension(activeBrowserUserKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            context.write(this.k, this.v);//输出
    }
}