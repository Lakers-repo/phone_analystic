/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DateDimension
 * Author:   14751
 * Date:     2018/9/20 1:56
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.base;

import com.phone.Util.TimeUtil;
import com.phone.common.DateEnum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈日期维度〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class DateDimension extends BaseDimension {
    private int id;
    private int year;
    private int season;//季度
    private int month;
    private int week;
    private int day;
    private Date calendar = new Date();
    private String type; //什么类型的指标，如天指标，月指标

    public DateDimension() {
    }

    public DateDimension(int year, int season, int month, int week, int day) {
        this.year = year;
        this.season = season;
        this.month = month;
        this.week = week;
        this.day = day;
    }

    public DateDimension(int year, int season, int month, int week, int day, Date calendar) {
       this(year,season,month,week,day);
        this.calendar = calendar;
    }

    public DateDimension(int year, int season, int month, int week, int day, Date calendar, String type) {
       this(year,season,month,week,day,calendar);
        this.type = type;
    }

    public DateDimension(int id, int year, int season, int month, int week, int day, Date calendar, String type) {
        this(year,season,month,week,day,calendar,type);
        this.id = id;
    }

    /**
     * 根据时间戳(毫秒)和type获取时间的维度
     * @param time
     * @param type
     * @return
     */
    public static DateDimension buildDate(long time,DateEnum type){
        int year = TimeUtil.getDateInfo(time,DateEnum.YEAR);
        Calendar calendar = Calendar.getInstance();
        //清空calendar(日历对象)
        calendar.clear();
        //判断type的类型
        if(type.equals(DateEnum.YEAR)){ //年指标，指该年的1月1号
           calendar.set(year,0,1);
            //月写成0---代表1月
            return new DateDimension(year,1,0,0,1,calendar.getTime(),type.type);
        }
        int season = TimeUtil.getDateInfo(time,DateEnum.SEASON);
        if(type.equals(DateEnum.SEASON)){//当季度的第一个月的1号这一天
            int month = season*3 - 2;
            calendar.set(year,month-1,1);// 月从0开始
            return new DateDimension(year,season,month,0,1,calendar.getTime(),type.type);//为了给客户展示
        }
        int month = TimeUtil.getDateInfo(time,DateEnum.MONTH);
        if(type.equals(DateEnum.MONTH)){//当月1号这一天
            calendar.set(year,month-1,1);
            return new DateDimension(year,season,month,0,1,calendar.getTime(),type.type);
        }
        int week = TimeUtil.getDateInfo(time,DateEnum.WEEK);
        if(type.equals(DateEnum.WEEK)){//当周的第一天的0时0分0秒
            long firstDayOfWeek = TimeUtil.getFirstDayOfWeek(time);
            year = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.YEAR);
            season  = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.SEASON);
            month = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.MONTH);
            week = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.WEEK);
            int day = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.DAY);
            calendar.set(year,month-1,day);
            return new DateDimension(year,season,month,week,day,calendar.getTime(),type.type);
        }
        int day = TimeUtil.getDateInfo(time,DateEnum.DAY);
        if(type.equals(DateEnum.DAY)){//当月1号这一天
            calendar.set(year,month-1,day);
            return new DateDimension(year,season,month,week,day,calendar.getTime(),type.type);
        }

       throw new RuntimeException("该type暂时不支持获取时间维度"+type.type);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeInt(year);
        dataOutput.writeInt(season);
        dataOutput.writeInt(month);
        dataOutput.writeInt(week);
        dataOutput.writeInt(day);
        dataOutput.writeLong(calendar.getTime());
        dataOutput.writeUTF(type);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.year = dataInput.readInt();
        this.season = dataInput.readInt();
        this.month = dataInput.readInt();
        this.week = dataInput.readInt();
        this.day = dataInput.readInt();
//        this.calendar = new Date(dataInput.readLong());
        this.calendar.setTime(dataInput.readLong());
        this.type = dataInput.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateDimension that = (DateDimension) o;

        if (id != that.id) return false;
        if (year != that.year) return false;
        if (season != that.season) return false;
        if (month != that.month) return false;
        if (week != that.week) return false;
        if (day != that.day) return false;
        if (calendar != null ? !calendar.equals(that.calendar) : that.calendar != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    //注意这里的Data不用比较了，因为前面已经将年月日比较结束了，结果自然就出来啦
    public int compareTo(BaseDimension o) {
        if(this == o){
            return 0;
        }
        DateDimension other = (DateDimension) o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.year - other.year;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.season - other.season;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.month - other.month;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.week - other.week;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.day - other.day;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.type.compareTo(other.type);
        return tmp;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + year;
        result = 31 * result + season;
        result = 31 * result + month;
        result = 31 * result + week;
        result = 31 * result + day;
        result = 31 * result + (calendar != null ? calendar.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date getCalendar() {
        return calendar;
    }

    public void setCalendar(Date calendar) {
        this.calendar = calendar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

