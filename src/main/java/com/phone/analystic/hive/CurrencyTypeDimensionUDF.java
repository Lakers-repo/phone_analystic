/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CurrencyTypeDimensionUDF
 * Author:   14751
 * Date:     2018/10/5 11:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.hive;

import com.phone.analystic.modle.base.CurrencyTypeDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈货币类型〉
 *
 * @author 14751
 * @create 2018/10/5 
 * @since 1.0.0
 */
public class CurrencyTypeDimensionUDF extends UDF{
    IDimension iDimension = new IDimensionImpl();
    public int evaluate(String name){
        if(StringUtils.isEmpty(name)){
            name = GlobalConstants.DEFAULT_VALUE;
        }
        int id = -1;
        CurrencyTypeDimension currencyTypeDimension = new CurrencyTypeDimension(name);
        try {
            id = iDimension.getDimensionIdByObject(currencyTypeDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}