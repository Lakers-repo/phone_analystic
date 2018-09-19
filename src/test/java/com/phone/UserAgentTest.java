/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserAgentTest
 * Author:   14751
 * Date:     2018/9/19 10:02
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone;

import com.phone.etl.ip.UserAgentUtil;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉 
 *
 * @author 14751
 * @create 2018/9/19 
 * @since 1.0.0
 */
public class UserAgentTest {
    public static void main(String[] args){
        System.out.println(UserAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:62.0) Gecko/20100101 Firefox/62.0"));
        System.out.println(UserAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36"));
        System.out.println(UserAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134"));
    }
}