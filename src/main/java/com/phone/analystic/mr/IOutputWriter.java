package com.phone.analystic.mr;


import com.phone.analystic.modle.StatsBaseDimension;
import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.analystic.mr.service.IDimension;
import org.apache.hadoop.conf.Configuration;

import java.sql.PreparedStatement;

/**
 * 操作结果表的接口---将结果写入到MySQL中
 */
public interface IOutputWriter {
    //输入的key和value的类型必须是基类，以便于其他指标的是实现---代码的健壮性

    /**
     * 为每一个kpi的最终结果赋值的接口
     * @param conf
     * @param key
     * @param value
     * @param ps
     * @param iDimension
     */
    void output(Configuration conf, StatsBaseDimension key,
                StatsOutPutValue value, PreparedStatement ps, IDimension iDimension);
}
