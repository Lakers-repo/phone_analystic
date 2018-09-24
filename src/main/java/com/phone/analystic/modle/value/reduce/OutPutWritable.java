/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: OutPutWritable
 * Author:   14751
 * Date:     2018/9/20 17:10
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.value.reduce;

import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.common.KpiType;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈reduce端输出的value的类型〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class OutPutWritable extends StatsOutPutValue {
    private KpiType Kpi;
    private MapWritable value = new MapWritable();

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        WritableUtils.writeEnum(dataOutput,Kpi);
        this.value.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        WritableUtils.readEnum(dataInput,KpiType.class);
        this.value.readFields(dataInput);
    }

    @Override
    //这里必须有返回值，通过KPI可以输出到对应的
    public KpiType getKpi() {
        return this.Kpi;
    }

    public void setKpi(KpiType kpi) {
        Kpi = kpi;
    }

    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }
}