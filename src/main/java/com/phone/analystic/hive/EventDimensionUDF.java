/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: EventDimensionUDF
 * Author:   14751
 * Date:     2018/9/27 21:16
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.hive;

import com.phone.analystic.modle.base.EventDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;
import com.phone.common.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈事件维度id〉
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class EventDimensionUDF extends UDF{
    IDimension iDimension = new IDimensionImpl();
    public int evaluate(String category,String action){
        if(StringUtils.isEmpty(category)){
            category = action = GlobalConstants.DEFAULT_VALUE;
        }
        if(StringUtils.isEmpty(action)){
            action = GlobalConstants.DEFAULT_VALUE;
        }

        int id = -1;
        EventDimension eventDimension = new EventDimension(category,action);

        try {
            id = iDimension.getDimensionIdByObject(eventDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}