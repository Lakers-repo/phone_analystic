/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: IDimensionImpl
 * Author:   14751
 * Date:     2018/9/20 17:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.mr.service.impl;

import com.phone.Util.JdbcUtil;
import com.phone.analystic.modle.base.*;
import com.phone.analystic.mr.service.IDimension;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈获取基础维度id的实现〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class IDimensionImpl implements IDimension{
    private static final Logger logger = Logger.getLogger(IDimensionImpl.class);
    //维度：维度对应的id 缓存 --- 相同维度 直接取缓存中的id，不需要从数据库中查询
    private Map<String,Integer> cache = new LinkedHashMap<String,Integer>(){
        @Override
        //移除比较老的数据，最多存储5000个
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return this.size() > 5000;
        }
    };
    /**
     * 0、先查询缓存中是否存在对应维度，如果有直接取出返回（比较难理解）
     * 1、根据维度对象里面的属性值，赋值给对应的sql，然后查询，如果有则返回对应的维度Id.
     * 2、如果查询没有，则先添加到数据库中并返回对应的id值.
     *
     * @param dimension 基础维度的对象
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public int getDimensionIdByObject(BaseDimension dimension) throws IOException, SQLException {
        String cacheKey = this.buildCache(dimension);
        if(this.cache.containsKey(cacheKey)){
            return this.cache.get(cacheKey);
        }

        //代码走到这里，缓存中没有，去查询数据库
        String[] sqls = null;
        if(dimension instanceof DateDimension){
            sqls = this.buildDateSqls();
        }else if(dimension instanceof  PlatformDimension){
            sqls = this.buildPlatformSqls();
        }else if(dimension instanceof BrowserDimension){
            sqls = this.buildBrowserSqls();
        } else if(dimension instanceof KPIDimension) {
            sqls = this.buildKpiSqls();
        }else if(dimension instanceof LocationDimension){
            sqls = this.buildLocalSqls();
        } else if(dimension instanceof EventDimension){
            sqls = this.buildEventSqls();
        } else if(dimension instanceof CurrencyTypeDimension){
            sqls = this.buildCurrencySqls(dimension);
        } else if(dimension instanceof PaymentTypeDimension){
            sqls = this.buildPaymentSqls(dimension);
        }

        Connection conn = JdbcUtil.getConn();
        int id = -1;
        synchronized (this){
            id = this.execute(sqls,dimension,conn);
        }
        //将获取到的id添加到缓存
        this.cache.put(cacheKey,id);
        return id;
    }

    /**
     *构建sqls
     * @return
     */
    private String[] buildDateSqls() {
        String select = "select `id` from `dimension_date` where `year` = ? and `season` = ? and `month` = ? and `week` = ? and `day` = ? and `calendar` = ? and `type` = ?";
        String insert = "insert into `dimension_date`(`year`,`season`, `month`,`week`,`day`,`calendar`,`type`) values(?,?,?,?,?,?,?)";
        return new String[]{select,insert};
    }

    private String[] buildPlatformSqls() {
        String select = "select `id` from `dimension_platform` where `platform_name` = ?";
        String insert = "insert into `dimension_platform`(`platform_name`) values(?)";
        return new String[]{select,insert};
    }

    private String[] buildBrowserSqls() {
        String select = "select `id` from `dimension_browser` where  `browser_name` = ? and `browser_version` = ?";
        String insert = "insert into `dimension_browser`(`browser_name`,`browser_version`) values(?,?)";
        return new String[]{select,insert};
    }

    private String[] buildKpiSqls() {
        String select = "select `id` from `dimension_kpi` where `kpi_name` = ?";
        String insert = "insert into `dimension_kpi`(`kpi_name`) values(?)";
        return new String[]{select,insert};
    }

    private String[] buildPaymentSqls(BaseDimension dimension) {
        String query = "select id from `dimension_payment_type` where `payment_type` = ?";
        String insert = "insert into `dimension_payment_type`(`payment_type`) values(?)";
        return new String[]{query,insert};
    }

    private String[] buildCurrencySqls(BaseDimension dimension) {
        String query = "select id from `dimension_currency_type` where `currency_name` = ?";
        String insert = "insert into `dimension_currency_type`(`currency_name`) values(?)";
        return new String[]{query,insert};
    }

    private String[] buildEventSqls() {
        String select = "select `id` from `dimension_event` where `category` = ? and `action` = ?";
        String insert = "insert into `dimension_event`(`category` ,`action`) values(?,?)";
        return new String[]{select,insert};
    }

    private String[] buildLocalSqls() {
        String select = "select `id` from `dimension_location` where `country` = ? and `province` = ? and `city` = ?";
        String insert = "insert into `dimension_location`(`country` ,`province`,`city`) values(?,?,?)";
        return new String[]{select,insert};
    }


    //返回缓存中的key，进行比较，确认缓存中有没有对应的key
    private String buildCache(BaseDimension dimension) {
        StringBuffer sb = new StringBuffer();
        if(dimension instanceof DateDimension){
            sb.append("date_");//为了防止不同维度的name名称一样
            DateDimension date = (DateDimension) dimension;
            sb.append(date.getYear()).append(date.getSeason()).append(date.getMonth())
                    .append(date.getDay()).append(date.getType());
        }else if(dimension instanceof PlatformDimension){
            sb.append("platform_");
            PlatformDimension platform = (PlatformDimension) dimension;
            sb.append(platform.getPlatformName());
        }else if(dimension instanceof BrowserDimension){
            sb.append("browser_");
            BrowserDimension browser = (BrowserDimension) dimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVserion());
        }else if(dimension instanceof KPIDimension){
            sb.append("kpi_");
            KPIDimension kpi = (KPIDimension) dimension;
            sb.append(kpi.getKpiName());
        }else if(dimension instanceof LocationDimension){
            sb.append("local_");
            LocationDimension local = (LocationDimension) dimension;
            sb.append(local.getCountry());
            sb.append(local.getProvince());
            sb.append(local.getCity());
        } else if(dimension instanceof EventDimension){
            sb.append("event_");
            EventDimension event = (EventDimension) dimension;
            sb.append(event.getCategory());
            sb.append(event.getAction());
        } else if(dimension instanceof PaymentTypeDimension){
            sb.append("payment_");
            PaymentTypeDimension payment = (PaymentTypeDimension) dimension;
            sb.append(payment.getPaymentType());
        } else if(dimension instanceof CurrencyTypeDimension){
            sb.append("currency_");
            CurrencyTypeDimension currency = (CurrencyTypeDimension) dimension;
            sb.append(currency.getCurrencyName());
        }
        return sb.length() == 0 ? null:sb.toString();
    }

    /**
     * 执行SQL语句
     */
    private int execute(String[] sqls, BaseDimension dimension, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            //先查询
            ps = conn.prepareStatement(sqls[0]);
            this.setArgs(dimension,ps);//为查询语句赋值
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);//就查询一个字段，也可以写id
            }
            //查询不到，然后插入在取值
            ps = conn.prepareStatement(sqls[1],Statement.RETURN_GENERATED_KEYS);
            this.setArgs(dimension,ps);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e){
            logger.warn("执行维度SQL异常",e);
        }finally {
            JdbcUtil.close(conn,ps,rs);
        }
        return -1;
    }

    /**
     * 设置参数---设置到mysql维度表中
     * @param dimension
     * @param ps
     */
    private void setArgs(BaseDimension dimension, PreparedStatement ps) {
        try{
            int i = 0;
            if(dimension instanceof DateDimension){
                DateDimension date = (DateDimension) dimension;
                ps.setInt(++i,date.getYear());
                ps.setInt(++i,date.getSeason());
                ps.setInt(++i,date.getMonth());
                ps.setInt(++i,date.getWeek());
                ps.setInt(++i,date.getDay());
                ps.setDate(++i,new Date(date.getCalendar().getTime()));
                ps.setString(++i,date.getType());
            }else if(dimension instanceof PlatformDimension){
            PlatformDimension platform = (PlatformDimension) dimension;
            ps.setString(++i,platform.getPlatformName());
        } else if(dimension instanceof BrowserDimension){
            BrowserDimension browser = (BrowserDimension) dimension;
            ps.setString(++i,browser.getBrowserName());
            ps.setString(++i,browser.getBrowserVserion());
        } else if(dimension instanceof KPIDimension) {
                KPIDimension kpi = (KPIDimension) dimension;
                ps.setString(++i, kpi.getKpiName());
            }else if(dimension instanceof LocationDimension){
                LocationDimension local = (LocationDimension) dimension;
                ps.setString(++i,local.getCountry());
                ps.setString(++i,local.getProvince());
                ps.setString(++i,local.getCity());
            } else if(dimension instanceof EventDimension){
                EventDimension event = (EventDimension) dimension;
                ps.setString(++i,event.getCategory());
                ps.setString(++i,event.getAction());
            } else if(dimension instanceof PaymentTypeDimension){
                PaymentTypeDimension payment = (PaymentTypeDimension) dimension;
                ps.setString(++i,payment.getPaymentType());
            } else if(dimension instanceof CurrencyTypeDimension){
                CurrencyTypeDimension currency = (CurrencyTypeDimension) dimension;
                ps.setString(++i,currency.getCurrencyName());
            }
        } catch(SQLException e){
            logger.warn("设置参数异常",e);
        }
    }
}