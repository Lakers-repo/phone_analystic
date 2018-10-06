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
package com.phone.analystic.mr.activevipuser;

import com.phone.analystic.modle.StatsCommonDimension;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.base.BrowserDimension;
import com.phone.analystic.modle.base.DateDimension;
import com.phone.analystic.modle.base.KPIDimension;
import com.phone.analystic.modle.base.PlatformDimension;
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
 * 〈ActiveMemberUserMapper---mapper函数 简单的封装〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 * 用户模块下的活跃会员---当天PV事件中去重的UMID个数
 */
public class ActiveMemberUserMapper extends Mapper<LongWritable,Text,StatsUserDimension,TimeOutPutValue> {
    private static final Logger logger = Logger.getLogger(ActiveMemberUserMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutPutValue v = new TimeOutPutValue();

    private KPIDimension activeMemberUserKpi = new KPIDimension(KpiType.ACTIVE_MEMBER.kpiName);
    private KPIDimension activeBrowserMemberUserKpi = new KPIDimension(KpiType.BROWSER_ACTIVE_MEMBER.kpiName);
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
        if(StringUtils.isNotEmpty(en) && en.equals(EventLogContant.EventEnum.PAGEVIEW.alias)){
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];
            String umid = fields[4];
            String browserName = fields[24];
            String browserVersion = fields[25];

            if(StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(umid)){
                logger.info("serverTime & active_mem_umid is null serverTime:"+serverTime+".active_mem_umid"+umid);
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

            //设置默认的浏览器对象(因为新增用户指标并不需要浏览器维度，所以赋值为null)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("","");
            statsCommonDimension.setKpiDimension(activeMemberUserKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(umid); //构建输出的value(并不需要时间time)
            context.write(this.k,this.v);//输出

            statsCommonDimension.setKpiDimension(activeBrowserMemberUserKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName,browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            context.write(this.k,this.v);//输出
        }
    }
}