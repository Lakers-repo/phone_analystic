package com.phone;

import com.phone.etl.IpUtil;
import com.phone.etl.ip.IPSeeker;

/**
 * @ClassName IpTest
 * @Author lyd
 * @Date $ $
 * @Vesion 1.0
 * @Description //TODO $
 **/
public class IpTest {
    public static void main(String[] args) {
        System.out.println(IPSeeker.getInstance().getCountry("112.111.11.12"));

        System.out.println(IpUtil.getRegionInfoByIp("112.111.11.12"));

        try {
            System.out.println(IpUtil.parserIp1("http://ip.taobao.com/service/getIpInfo.php?ip=112.111.11.12","utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}