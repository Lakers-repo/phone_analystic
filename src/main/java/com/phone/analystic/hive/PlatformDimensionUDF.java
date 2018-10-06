/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PlatformDimensionUDF
 * Author:   14751
 * Date:     2018/9/27 21:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.hive;

import com.phone.analystic.modle.base.PlatformDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈平台维度 udf〉
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class PlatformDimensionUDF extends UDF {

    IDimension iDimension = new IDimensionImpl();

    /**
     * 事件维度的id
     * @param platform
     * @return
     */
    public int evaluate(String platform){
        if(StringUtils.isEmpty(platform)){
            platform = GlobalConstants.DEFAULT_VALUE;
        }

        int id = -1;


            PlatformDimension platformDimension = new PlatformDimension(platform);
        try {
            id = iDimension.getDimensionIdByObject(platformDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void main(String[] args){
        System.out.println(new PlatformDimensionUDF().evaluate("website"));
    }
}