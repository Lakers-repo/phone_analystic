/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DateEnum
 * Author:   14751
 * Date:     2018/9/20 2:05
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.common;

/**
 * 〈一句话功能简述〉<br> 
 * 〈时间枚举〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public enum DateEnum {
    YEAR("year"),
    SEASON("season"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour");

    public String type;

    DateEnum(String type){
        this.type = type;
    }

    /**
     * 根据type获取时间枚举
     * @param type
     * @return
     */
    public DateEnum valueOfType(String type){
        for(DateEnum date: values()){
            if(type.equals(date.type)){
                return date;
            }
        }
        throw  new RuntimeException("该type暂不支持获取时间枚举"+type);
    }
}