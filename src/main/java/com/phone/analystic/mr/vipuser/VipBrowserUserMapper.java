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
package com.phone.analystic.mr.vipuser;

import com.phone.Util.JdbcUtil;
import com.phone.Util.MemberUtil;
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
import java.sql.Connection;

/**
 * 〈一句话功能简述〉<br> 
 * 〈VipBrowserUserMapper---mapper函数 简单的封装〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 * 浏览器模块下的新增会员
 */
public class VipBrowserUserMapper extends Mapper<LongWritable,Text,StatsUserDimension,TimeOutPutValue> {
    private static final Logger logger = Logger.getLogger(VipBrowserUserMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutPutValue v = new TimeOutPutValue();
    private Connection conn = null;//获取数据库连接

    private KPIDimension newBrowserMemberKpi = new KPIDimension(KpiType.BROWSER_NEW_MEMBER.kpiName);

    //该方法在map方法前，只执行一次
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        conn = JdbcUtil.getConn();
        MemberUtil.deleteByDay(context.getConfiguration(),conn);//记得一定要将conn传过去
    }

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
        if(StringUtils.isNotEmpty(en)){
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];
            String umid = fields[4];
            String browserName = fields[24];
            String browserVersion = fields[25];
            if(StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(umid)){
                logger.info("serverTime & Browser_umid is null serverTime:"+serverTime+".Browser_umid"+umid);
                return;
            }

            //判断会员id是否是新会员---!表示与MemberUtil返回的结果相反（注意点）
            if(!MemberUtil.checkMemberId(umid)){
                logger.info("umid is invalid.memberId:"+umid);
            }

            //判断会员id是否是新会员(重点)---!表示与MemberUtil返回的结果相反（注意点）
            if(!MemberUtil.isNewMember(umid,conn,context.getConfiguration())){
                logger.info("umid is not new memberId.memberId:"+umid);
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
            statsCommonDimension.setKpiDimension(newBrowserMemberKpi);

            BrowserDimension browserDimension = new BrowserDimension(browserName,browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);

            //构建输出的value(并不需要时间time)
            this.v.setId(umid);
            this.v.setTime(stime);

            //输出
            context.write(this.k,this.v);
        }
    }

    //map方法后，只执行一次
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        JdbcUtil.close(conn,null,null);
    }
}