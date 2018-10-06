/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: MemberUtil
 * Author:   14751
 * Date:     2018/9/25 20:44
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.Util;

import com.phone.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈查看会员id是否是新增会员，建议过滤不合法的会员id〉
 *
 * @author 14751
 * @create 2018/9/25 
 * @since 1.0.0
 */
public class MemberUtil {
    private static final Logger logger = Logger.getLogger(MemberUtil.class);
    //对于同一个memberId，只需要取连接数据库一次，减少了网络IO的通信
    private static Map<String, Boolean> cache = new LinkedHashMap<String, Boolean>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return super.removeEldestEntry(eldest);
        }
    };

    /**
     * 检查会员id是否合法
     */
    public static boolean checkMemberId(String memberId){
        String regex = "^[0-9a-zA-Z].*$";
        if(StringUtils.isNotEmpty(memberId)){
            return memberId.trim().matches(regex);
        }
        return false;
    }

    /**
     * 是否是一个新增的会员
     * res为true是新增会员，false不是新增会员
     */
    public static boolean isNewMember(String memberId, Connection conn, Configuration conf){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Boolean res = false;
        try {
            res = cache.get(memberId);
            if(res == null){
                String sql = conf.get(GlobalConstants.PREFIX_TOTAL+"member_info");
                ps = conn.prepareStatement(sql);
                ps.setString(1,memberId);
                rs = ps.executeQuery();
                if(rs.next()){
                    res = false;
                }else {
                    res = true;
                }
                //添加到cache中
                cache.put(memberId,res);
            }
        } catch (SQLException e) {
            logger.warn("SQL语句执行失败！！！",e);
        }
        return res == null? false: res.booleanValue();
    }

    /**
     * 删除某一天的会员，防止重新跑某一天的新增会员
     */
    public static void deleteByDay(Configuration conf,Connection conn){
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(conf.get("total_delete_member_info"));
            ps.setDate(1,new Date(TimeUtil.parseString2Long(conf.get(GlobalConstants.RUNNING_DATE))));
            ps.execute();
        }catch (SQLException e){
            logger.warn("执行SQL语句失败！！！",e);
        }finally {
            JdbcUtil.close(null,ps,null);//这里不能提前关闭资源
        }
    }
}