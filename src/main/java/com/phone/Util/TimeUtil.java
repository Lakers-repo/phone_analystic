/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: TimeUtil
 * Author:   14751
 * Date:     2018/9/19 19:34
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.Util;

import com.phone.common.DateEnum;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br> 
 * 〈时间工具类〉
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 */
public class TimeUtil {
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";
    /**
     * 判断时间是否有效
     */
    public static boolean isValidateDate(String date){
        Matcher matcher = null;
        Boolean res = false;
        String regexp = "^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}";//简单验证一下时间是否有效
        if (StringUtils.isNotEmpty(date)) {
            Pattern pattern = Pattern.compile(regexp);//编译正则表达式
            matcher = pattern.matcher(date);
        }
        if (matcher != null) {
            res = matcher.matches();
        }
        return res;
    }
    /**
     * 获取昨天的日期 yyyy-MM-dd
     */
    public static String getYesterday(){
        return getYesterday(DEFAULT_FORMAT);
    }

    /**
     * 获取指定格式的昨天的日期
     * @param pattern
     * @return
     */
    private static String getYesterday(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();//获取日历对象
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        return sdf.format(calendar.getTime());
    }

    /**
     * 将时间戳转换成默认格式的日期
     * 注意点：
     * format---将时间戳转换成指定日期的格式
     * parse---将指定时间格式转换成时间戳
     * @param time
     * @return
     */
    public static String parseLong2String(long time) {
        return parseLong2String(time, DEFAULT_FORMAT);
    }

    public static String parseLong2String(long time, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

//    /**
//     * 将当前时间转换成指定格式的日期（字符串）
//     * @param pattern
//     * @return
//     */
//    public static  String parseLongToString(String pattern){
//        Calendar calendar = Calendar.getInstance();
//        return new SimpleDateFormat(pattern).format(calendar.getTime());
//    }


    /**
     * 将默认的日期格式转换成时间戳
     *
     * @param date
     * @return
     */
    public static long parseString2Long(String date) {
        return parseString2Long(date, DEFAULT_FORMAT);
    }

    public static long parseString2Long(String date, String pattern) {
        Date dt = null;

        try {
            dt = new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt.getTime();
    }

    /**
     * 获取日期信息
     * @param time
     * @param type
     * @return
     */
    public static int getDateInfo(long time, DateEnum type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if(type.equals(DateEnum.YEAR)){
            return calendar.get(Calendar.YEAR);
        }
        if(type.equals(DateEnum.SEASON)){
            int month = calendar.get(Calendar.MONTH) + 1;//返回从0到11，所以需要加1
            return month % 3 == 0 ? month / 3 : (month / 3 + 1);
        }
        if(type.equals(DateEnum.MONTH)){
            return calendar.get(Calendar.MONTH) + 1;
        }
        if(type.equals(DateEnum.WEEK)){
            return calendar.get(Calendar.WEEK_OF_YEAR);
        }
        if(type.equals(DateEnum.DAY)){
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        if(type.equals(DateEnum.HOUR)){
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
        throw  new RuntimeException("不支持该类型的日期信息获取.type："+type.type);

    }

    /**
     * 获取某周第一天时间戳
     * @param time
     * @return
     */
    public static long getFirstDayOfWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //
        calendar.set(Calendar.DAY_OF_WEEK, 1);//该周的第一天,0时0分0秒0毫秒（24小时制）
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}