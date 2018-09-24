/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: KpiType
 * Author:   14751
 * Date:     2018/9/20 14:42
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.common;

/**
 * 〈一句话功能简述〉<br> 
 * 〈统计指标的枚举〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public enum  KpiType {

    NEW_USER("new_user"),
    BROWSER_NEW_USER("browser_new_user"),
    ACTIVE_USER("active_user"),
    BROWSER_ACTIVE_USER("browser_active_user"),
    ACTIVE_MEMBER("active_member"),
    BROWSER_ACTIVE_MEMBER("browser_active_member"),
    NEW_MEMBER("new_member"),
    BROWSER_NEW_MEMBER("browser_new_member");

    public String kpiName;

    KpiType(String kpiName) {
        this.kpiName = kpiName;
    }

    /**
     * 根据Kpi的name获取对应的指标
     */
    public static KpiType valueOfKpiName(String name){
        for (KpiType Kpi: values()) {
            if(Kpi.kpiName.equals(name)){
                return Kpi;
            }
        }
        return null;//循环遍历所有的Kpi，如果没有找到就返回null，或者抛出异常
    }
}