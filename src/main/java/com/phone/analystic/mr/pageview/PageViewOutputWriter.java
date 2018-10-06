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
package com.phone.analystic.mr.pageview;

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
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

/**
 * 〈一句话功能简述〉<br> 
 * 〈对于不同的指标，这列赋值都是不一样的〉
 *
 * @author 14751
 * @create 2018/9/21 
 * @since 1.0.0
 */
public class PageViewOutputWriter implements IOutputWriter {
    private static final Logger logger = Logger.getLogger(PageViewOutputWriter.class);
    @Override
    //这里通过key和value给ps语句赋值
    public void output(Configuration conf, StatsBaseDimension key, StatsOutPutValue value, PreparedStatement ps, IDimension iDimension) {

        try {
            StatsUserDimension k = (StatsUserDimension) key;
            OutPutWritable v = (OutPutWritable) value;

            //获取pageview事件中每个url的访问量count
            int pageviewCount = ((IntWritable)(v.getValue().get(new IntWritable(-1)))).get();

            int i = 0;
            ps.setInt(++i,iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(k.getBrowserDimension()));
            ps.setInt(++i,pageviewCount);
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,pageviewCount);

            ps.addBatch();//添加到批处理中，批量执行SQL语句
        } catch (Exception e) {
            logger.warn("给ps赋值失败！！！");
        }
    }
}