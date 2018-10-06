/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PaymentTypeDimensionUDF
 * Author:   14751
 * Date:     2018/10/5 11:55
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.hive;

import com.phone.analystic.modle.base.CurrencyTypeDimension;
import com.phone.analystic.modle.base.PaymentTypeDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈支付平台〉
 *
 * @author 14751
 * @create 2018/10/5 
 * @since 1.0.0
 */
public class PaymentTypeDimensionUDF extends UDF{
    IDimension iDimension = new IDimensionImpl();
    public int evaluate(String name){
        if(StringUtils.isEmpty(name)){
            name = GlobalConstants.DEFAULT_VALUE;
        }
        int id = -1;
        PaymentTypeDimension paymentTypeDimension = new PaymentTypeDimension(name);
        try {
            id = iDimension.getDimensionIdByObject(paymentTypeDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}