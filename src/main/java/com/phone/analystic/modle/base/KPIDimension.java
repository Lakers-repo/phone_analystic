/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: KPIDimension
 * Author:   14751
 * Date:     2018/9/20 9:58
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈KPI维度〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class KPIDimension extends BaseDimension{
    private int id;
    private String kpiName;

    public KPIDimension() {
    }

    public KPIDimension(String kpiName) {
        this.kpiName = kpiName;
    }

    public KPIDimension(int id, String kpiName) {
        this(kpiName);
        this.id = id;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(kpiName);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.kpiName = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
       if(this == o){
           return 0;
       }
       KPIDimension other = (KPIDimension) o;
       int tmp = this.id - other.id;
       if(tmp != 0){
            return tmp;
       }
       return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KPIDimension that = (KPIDimension) o;

        if (id != that.id) return false;
        return kpiName != null ? kpiName.equals(that.kpiName) : that.kpiName == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (kpiName != null ? kpiName.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }
}