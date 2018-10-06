/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DateDimensionUDF
 * Author:   14751
 * Date:     2018/9/27 21:10
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.hive;

import com.phone.Util.TimeUtil;
import com.phone.analystic.modle.base.DateDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.DateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈时间维度id〉
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class DateDimensionUDF extends UDF {

    IDimension iDimension = new IDimensionImpl();
    public int evaluate(String date){
        if(StringUtils.isEmpty(date)){
            date = TimeUtil.getYesterday();
        }
        DateDimension dateDimension = DateDimension.buildDate(TimeUtil.parseString2Long(date), DateEnum.DAY);
        int id = -1;

        try {
            id = iDimension.getDimensionIdByObject(dateDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void main(String[] args){
        System.out.println(new DateDimensionUDF().evaluate("2018-09-22"));
    }
}