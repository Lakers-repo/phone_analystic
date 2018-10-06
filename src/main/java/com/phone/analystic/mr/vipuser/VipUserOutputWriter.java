/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: NewUserOutputWriter
 * Author:   14751
 * Date:     2018/9/21 23:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.vipuser;

import com.phone.Util.TimeUtil;
import com.phone.analystic.modle.StatsBaseDimension;
import com.phone.analystic.modle.StatsUserDimension;
import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.analystic.modle.value.reduce.OutPutWritable;
import com.phone.analystic.mr.IOutputWriter;
import com.phone.analystic.mr.service.IDimension;
import com.phone.common.GlobalConstants;
import com.phone.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.PreparedStatement;

/**
 * 〈一句话功能简述〉<br>
 * 〈对于不同的指标，这列赋值都是不一样的〉
 *
 * @author 14751
 * @create 2018/9/21
 * @since 1.0.0
 */
public class VipUserOutputWriter implements IOutputWriter {
    private static final Logger logger = Logger.getLogger(VipUserOutputWriter.class);

    @Override
    //这里通过key和value给ps语句赋值
    public void output(Configuration conf, StatsBaseDimension key, StatsOutPutValue value, PreparedStatement ps, IDimension iDimension) {

        try {
            StatsUserDimension k = (StatsUserDimension) key;
            OutPutWritable v = (OutPutWritable) value;

            //为ps赋值---判断KPI的值
            int i = 0;
            switch (v.getKpi()) {
                case BROWSER_NEW_MEMBER:
                case NEW_MEMBER:
                    int vipUser = ((IntWritable) (v.getValue().get(new IntWritable(-1)))).get();
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));
                    //修改1
                    if (v.getKpi().equals(KpiType.BROWSER_NEW_MEMBER)) {
                        ps.setInt(++i, iDimension.getDimensionIdByObject(k.getBrowserDimension()));
                    }
                    ps.setInt(++i, vipUser);
                    ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));//注意这里需要在runner类里面进行赋值
                    ps.setInt(++i, vipUser);
                    break;
                case MEMBER_INFO:
                    String memberId = ((Text) (v.getValue().get(new IntWritable(-2)))).toString();
                    long minTime = ((LongWritable)(v.getValue().get(new IntWritable(-3)))).get();
                    ps.setString(++i, memberId);
                    ps.setDate(++i, new Date(TimeUtil.parseString2Long(conf.get(GlobalConstants.RUNNING_DATE))));
                    ps.setLong(++i, minTime);
                    ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));
                    ps.setDate(++i, new Date(TimeUtil.parseString2Long(conf.get(GlobalConstants.RUNNING_DATE))));
                    break;
                    default:
                        throw new RuntimeException("没有此KPI，请检查程序！！！");
            }
            ps.addBatch();//添加到批处理中，批量执行SQL语句
        } catch (Exception e) {
            logger.warn("给ps赋值失败！！！",e);
        }
    }
}