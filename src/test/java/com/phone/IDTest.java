/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: IDTest
 * Author:   14751
 * Date:     2018/9/21 9:09
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone;

import com.phone.analystic.modle.base.PlatformDimension;
import com.phone.analystic.mr.service.IDimension;
import com.phone.analystic.mr.service.impl.IDimensionImpl;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉 
 *
 * @author 14751
 * @create 2018/9/21 
 * @since 1.0.0
 */
public class IDTest {
    public static void main(String[] args){
        PlatformDimension pl = new PlatformDimension("android");
        IDimension impl = new IDimensionImpl();
        try {
           System.out.println(impl.getDimensionIdByObject(pl));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}