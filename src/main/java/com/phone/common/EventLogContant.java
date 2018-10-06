/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: EventLogContant
 * Author:   14751
 * Date:     2018/9/19 0:27
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.common;

/**
 * 〈一句话功能简述〉<br> 
 * 〈定义采集的数据中的相对应的key〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 */
public class EventLogContant {
    /**
     * 定义事件枚举（两种写法）
     * 还有一种是像定义属性一样，直接事件 = 别名
     */

    public static enum EventEnum{
        //最后一个字段你不可以随便写（注意点）
        LAUNCH(1,"launch_event","e_l"),
        PAGEVIEW(2,"page_view_event","e_pv"),
        CHARGEREQUEST(3,"charge_request_event","e_crt"),
        CHAARGESUCCESS(4,"charge_success","e_cs"),
        CHARGEREFUND(5,"charge_refund","e_cr"),
        EVENT(6,"event","e_e");

        public final int id;
        public final String name;
        public final String alias;//别名

        EventEnum(int id,String name,String alias){
            this.id = id;
            this.name = name;
            this.alias = alias;
        }

        /**
         * 根据别名获取事件
         * @param alias
         * @return
         */
        public static EventEnum valueOfAlias(String alias){
            //for循环
            for(EventEnum event:values()){
                if(alias.equals(event.alias)){
                    return event;
                }
            }
//            return  null;
              throw new RuntimeException("该alias没有对应的异常");
        }
    }
    /**
     * 日志相关
     */
    public static final String EVENT_COLUMN_NAME_VERSION = "ver";

    public static final String EVENT_COLUMN_NAME_SERVER_TIME = "s_time";

    public static final String EVENT_COLUMN_NAME_EVENT_NAME = "en";

    public static final String EVENT_COLUMN_NAME_UUID = "u_ud";

    public static final String EVENT_COLUMN_NAME_MEMBER_ID = "u_mid";

    public static final String EVENT_COLUMN_NAME_SESSION_ID = "u_sd";

    public static final String EVENT_COLUMN_NAME_CLIENT_TIME = "c_time";

    private static final String EVENT_COLUMN_NAME_LANGUAGE = "l";

    public static final String EVENT_COLUMN_NAME_USERAGENT = "b_iev";

    public static final String EVENT_COLUMN_NAME_RESOLUTION = "b_rst";

    public static final String EVENT_COLUMN_NAME_CURRENT_URL = "p_url";

    public static final String EVENT_COLUMN_NAME_PREFFER_URL = "p_ref";

    public static final String EVENT_COLUMN_NAME_TITLE = "tt";

    public static final String EVENT_COLUMN_NAME_PLATFORM = "pl";

    public static final String EVENT_COLUMN_NAME_IP = "ip";

    public static final String EVENT_COLUMN_NAME_SEPARATOR = "\\^A";

    /**
     * 订单相关
     */
    public static final String EVENT_COLUMN_NAME_ORDER = "oid";

    public static final String EVENT_COLUMN_NAME_ORDER_NAME = "on";

    public static final String EVENT_COLUMN_NAME_CURRENCY_AMOUNT = "cua";

    public static final String EVENT_COLUMN_NAME_CURRENCY_TYPE = "cut";

    public static final String EVENT_COLUMN_NAME_PAYMENT_TYPE = "pt";

    /**
     * 事件相关
     */
    public static final String EVENT_COLUMN_NAME_EVENT_CATEGORY = "ca";

    public static final String EVENT_COLUMN_NAME_EVENT_ACTION = "ac";

    public static final String EVENT_COLUMN_NAME_EVENT_KV = "kv_";

    public static final String EVENT_COLUMN_NAME_EVENT_DURATION = "du";

    /**
     * 浏览器相关
     */
    public static final String EVENT_COLUMN_NAME_BROWSER_NAME = "browserName";

    public static final String EVENT_COLUMN_NAME_BORWSER_VERSION = "browserVersion";

    public static final String EVENT_COLUMN_NAME_OS_NAME = "osName";

    public static final String EVENT_COLUMN_NAME_OS_VERSION = "osVersion";

    /**
     *地域相关
     */
    public static final String EVENT_COLUMN_NAME_COUNTRY = "country";

    public static final String EVENT_COLUMN_NAME_PROVINCE = "province";

    public static final String EVENT_COLUMN_NAME_CITY = "city";

}