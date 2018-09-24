/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: StatsCommonDimension
 * Author:   14751
 * Date:     2018/9/20 13:57
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle;

import com.phone.analystic.modle.base.BaseDimension;
import com.phone.analystic.modle.base.DateDimension;
import com.phone.analystic.modle.base.KPIDimension;
import com.phone.analystic.modle.base.PlatformDimension;

import javax.swing.text.PlainDocument;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈公共维度类的封装---平台和时间维度〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class StatsCommonDimension extends StatsBaseDimension {
    private PlatformDimension platformDimension = new PlatformDimension();
    private DateDimension dateDimension = new DateDimension();
    private KPIDimension kpiDimension = new KPIDimension();

    public StatsCommonDimension() {
    }

    public StatsCommonDimension(PlatformDimension platformDimension, DateDimension dateDimension, KPIDimension kpiDimension) {
        this.platformDimension = platformDimension;
        this.dateDimension = dateDimension;
        this.kpiDimension = kpiDimension;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.dateDimension.write(dataOutput);
        this.platformDimension.write(dataOutput);
        this.kpiDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.dateDimension.readFields(dataInput);
        this.platformDimension.readFields(dataInput);
        this.kpiDimension.readFields(dataInput);
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this == o){
            return 0;
        }
        StatsCommonDimension other = (StatsCommonDimension) o;
        int tmp = this.dateDimension.compareTo(other.dateDimension);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.platformDimension.compareTo(other.platformDimension);
        if(tmp != 0){
            return tmp;
        }
        return this.kpiDimension.compareTo(other.kpiDimension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatsCommonDimension that = (StatsCommonDimension) o;

        if (platformDimension != null ? !platformDimension.equals(that.platformDimension) : that.platformDimension != null)
            return false;
        if (dateDimension != null ? !dateDimension.equals(that.dateDimension) : that.dateDimension != null)
            return false;
        return kpiDimension != null ? kpiDimension.equals(that.kpiDimension) : that.kpiDimension == null;
    }

    @Override
    public int hashCode() {
        int result = platformDimension != null ? platformDimension.hashCode() : 0;
        result = 31 * result + (dateDimension != null ? dateDimension.hashCode() : 0);
        result = 31 * result + (kpiDimension != null ? kpiDimension.hashCode() : 0);
        return result;
    }

    public PlatformDimension getPlatformDimension() {
        return platformDimension;
    }

    public void setPlatformDimension(PlatformDimension platformDimension) {
        this.platformDimension = platformDimension;
    }

    public DateDimension getDateDimension() {
        return dateDimension;
    }

    public void setDateDimension(DateDimension dateDimension) {
        this.dateDimension = dateDimension;
    }

    public KPIDimension getKpiDimension() {
        return kpiDimension;
    }

    public void setKpiDimension(KPIDimension kpiDimension) {
        this.kpiDimension = kpiDimension;
    }
}