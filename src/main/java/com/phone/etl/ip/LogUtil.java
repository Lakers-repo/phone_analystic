package com.phone.etl.ip;

import com.phone.common.EventLogContant;
import com.phone.etl.IpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LogUtil
 * @Author 奚海波
 * @Date $ $
 * @Vesion 1.0
 * @Description 将采集的一行一行的日志解析成key-value，便于存储
 **/
public class LogUtil {
    private static  final Logger logger = Logger.getLogger(LogUtil.class);

    public static Map<String,String> parserLog(String log){
        //定义一个map,Concurrent包里面的集合都是线程安全和高并发的
        Map<String,String> info = new ConcurrentHashMap<String,String>();
        if(StringUtils.isNotEmpty(log)){
            String[] fields = log.split(EventLogContant.EVENT_COLUMN_NAME_SEPARATOR);
            if(fields.length == 4){
                //给info赋值
                info.put(EventLogContant.EVENT_COLUMN_NAME_IP,fields[0]);
                info.put(EventLogContant.EVENT_COLUMN_NAME_SERVER_TIME,fields[1].replaceAll("\\.",""));

                //判断是否有参数列表
                int index = fields[3].indexOf("?");
                if(index > 0){
                    //获取参数列表
                    String params = fields[3].substring(index+1);
                    handleParams(info,params);
                    //解析IP
                    handleIP(info);
                    //解析浏览器
                    handleUserAgent(info);
                }
            }
        }
        return info;
    }

    /**
     * 解析useragent
     */
    private static void handleUserAgent(Map<String,String> info){
        if(info.containsKey(EventLogContant.EVENT_COLUMN_NAME_USERAGENT)){
            //注意传参不要传错
            UserAgentUtil.UserAgentInfo userAgent = new UserAgentUtil().parserUserAgent(info.get(EventLogContant.EVENT_COLUMN_NAME_USERAGENT));
            if(userAgent != null){
                info.put(EventLogContant.EVENT_COLUMN_NAME_BROWSER_NAME,userAgent.getBrowserName());
                info.put(EventLogContant.EVENT_COLUMN_NAME_BORWSER_VERSION,userAgent.getBrowserVersion());
                info.put(EventLogContant.EVENT_COLUMN_NAME_OS_NAME,userAgent.getOsName());
                info.put(EventLogContant.EVENT_COLUMN_NAME_OS_VERSION,userAgent.getOsVersion());
            }
        }
    }

    /**
     * 解析IP
     */
    private static void handleIP(Map<String,String> info){
        if(info.containsKey(EventLogContant.EVENT_COLUMN_NAME_IP)){
            IpUtil.RegionInfo region = new IpUtil().getRegionInfoByIp(info.get(EventLogContant.EVENT_COLUMN_NAME_IP));
            if(region != null){
                info.put(EventLogContant.EVENT_COLUMN_NAME_COUNTRY,region.getCountry());
                info.put(EventLogContant.EVENT_COLUMN_NAME_PROVINCE,region.getProvince());
                info.put(EventLogContant.EVENT_COLUMN_NAME_CITY,region.getCity());
            }
        }
    }
    /**
     * 将参数列表的k-v存储到info中
     */
    private static void handleParams(Map<String,String> info ,String params){
        if(StringUtils.isNotEmpty(params)){
            String[] paramkvs = params.split("&");
            try {
                for(String paramkv:paramkvs){
                    String[] kvs = paramkv.split("=");
                    String k = kvs[0];
                    //解码
                    String v = URLDecoder.decode(kvs[1],"utf-8");
                    if(StringUtils.isNotEmpty(k)){
                        info.put(k,v);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                logger.warn("处理参数列表异常",e);
            }
        }
    }
}