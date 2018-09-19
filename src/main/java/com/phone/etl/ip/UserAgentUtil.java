package com.phone.etl.ip;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @ClassName UserAgentUtil
 * @Author 奚海波
 * @Date $ $
 * @Vesion 1.0
 * @Description
 *
 * window.navigator.userAgent(在浏览器中获取数据，进行测试)
 **/
public class UserAgentUtil {
    public static final Logger logger = Logger.getLogger(UserAgentUtil.class);

    //获取uasparser对象
    private static UASparser uaSparser = null;

    //初始化
    static {
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            logger.error("获取usaparser对象失败！！！",e);
        }
    }

    /**
     * 解析浏览器的代理对象
     * @param userAgent
     * @return
     */
    public static UserAgentInfo parserUserAgent(String userAgent) {
        UserAgentInfo info = null;

        //使用uasparser获取对象代理信息
        try {
            if (StringUtils.isNotEmpty(userAgent)) {
                //导入包，不能使用自己的类，返回的也是UserAgentInfo
                cz.mallat.uasparser.UserAgentInfo ua = uaSparser.parse(userAgent);
                if (ua != null) {
                    info = new UserAgentInfo();

                    //为info设置信息
                    info.setBrowserName(ua.getUaFamily());//浏览器名称
                    info.setBrowserVersion(ua.getBrowserVersionInfo());//浏览器版本
                    info.setOsName(ua.getOsFamily());//操作系统名称
                    info.setOsVersion(ua.getOsName());//操作系统
                    }
                }
            } catch(IOException e){
                logger.error("useragent解析异常！！！", e);
            }
            return info;
        }

    /**
     * 用于封装useragent解析后的信息
     */
    public static class UserAgentInfo{
        private String browserName;
        private String browserVersion;
        private String osName;
        private String osVersion;

        public UserAgentInfo() {

        }

        public UserAgentInfo(String browserName, String browserVersion, String osName, String osVersion) {
            this.browserName = browserName;
            this.browserVersion = browserVersion;
            this.osName = osName;
            this.osVersion = osVersion;
        }

        public String getBrowserName() {
            return browserName;
        }

        public void setBrowserName(String browserName) {
            this.browserName = browserName;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        @Override
        public String toString() {
            return "UserAgentInfo{" +
                    "browserName='" + browserName + '\'' +
                    ", browserVersion='" + browserVersion + '\'' +
                    ", osName='" + osName + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    '}';
        }
    }
}