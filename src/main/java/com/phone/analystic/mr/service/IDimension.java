/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: IDimension
 * Author:   14751
 * Date:     2018/9/20 15:20
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.service;

import com.phone.analystic.modle.base.BaseDimension;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈根据维度获取对应的id的接口〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public interface IDimension {
    /**
     * 基础维度的对象
     * @param dimension
     * @return
     * @throws IOException
     * @throws SQLException
     */
    int getDimensionIdByObject(BaseDimension dimension) throws IOException,SQLException;
}