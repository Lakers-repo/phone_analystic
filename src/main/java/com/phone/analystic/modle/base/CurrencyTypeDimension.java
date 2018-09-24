/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CurrencyTypeDimension
 * Author:   14751
 * Date:     2018/9/24 23:29
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
 * 〈货币类型〉
 *
 * @author 14751
 * @create 2018/9/24 
 * @since 1.0.0
 */
public class CurrencyTypeDimension extends BaseDimension{
    private int id;
    private String currencyName;

    public CurrencyTypeDimension() {
    }

    public CurrencyTypeDimension(String currencyName) {
        this.currencyName = currencyName;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.currencyName);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.currencyName = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(this == o){
            return  0;
        }
        CurrencyTypeDimension other = (CurrencyTypeDimension) o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.currencyName.compareTo(other.currencyName);
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyTypeDimension that = (CurrencyTypeDimension) o;

        if (id != that.id) return false;
        return currencyName != null ? currencyName.equals(that.currencyName) : that.currencyName == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}