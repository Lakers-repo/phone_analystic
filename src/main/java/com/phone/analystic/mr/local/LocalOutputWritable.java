/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: LocalOutputWritable
 * Author:   14751
 * Date:     2018/9/27 1:49
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.local;

import com.phone.analystic.modle.StatsBaseDimension;
import com.phone.analystic.modle.StatsLocalDimension;
import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.analystic.modle.value.reduce.LocalOutputValue;
import com.phone.analystic.mr.IOutputWriter;
import com.phone.analystic.mr.service.IDimension;
import com.phone.common.GlobalConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

/**
 * 〈一句话功能简述〉<br> 
 * 〈LOCAL PS 赋值〉
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class LocalOutputWritable implements IOutputWriter{
    private static final Logger logger = Logger.getLogger(LocalOutputWritable.class);
    @Override
    public void output(Configuration conf, StatsBaseDimension key, StatsOutPutValue value, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsLocalDimension statsLocalDimension = (StatsLocalDimension) key;
            LocalOutputValue v = (LocalOutputValue) value;

            //为ps赋值
            int i = 0;
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocalDimension.getStatsCommonDimension().getDateDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocalDimension.getStatsCommonDimension().getPlatformDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocalDimension.getLocationDimension()));
            ps.setInt(++i,v.getActiveUser());
            ps.setInt(++i,v.getSessions());
            ps.setInt(++i,v.getBoundSessions());
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,v.getActiveUser());
            ps.setInt(++i,v.getSessions());
            ps.setInt(++i,v.getBoundSessions());

            //添加到批处理中
            ps.addBatch();
        } catch (Exception e) {
            logger.warn("为ps赋值失败！！！",e);
        }
    }
}