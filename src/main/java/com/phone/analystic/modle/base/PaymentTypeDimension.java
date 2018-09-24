/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PaymentTypeDimension
 * Author:   14751
 * Date:     2018/9/24 23:28
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
 * 〈支付类型〉
 *
 * @author 14751
 * @create 2018/9/24
 * @since 1.0.0
 */
public class PaymentTypeDimension extends BaseDimension {
    private int id;
    private String paymentType;

    public PaymentTypeDimension() {
    }

    public PaymentTypeDimension(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.paymentType);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.paymentType = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }
        PaymentTypeDimension other = (PaymentTypeDimension) o;
        int tmp = this.id - other.id;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.paymentType.compareTo(other.paymentType);
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentTypeDimension that = (PaymentTypeDimension) o;

        if (id != that.id) return false;
        return paymentType != null ? paymentType.equals(that.paymentType) : that.paymentType == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (paymentType != null ? paymentType.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}