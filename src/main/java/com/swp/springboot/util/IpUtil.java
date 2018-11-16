package com.swp.springboot.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述:
 * 获取IP
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-26 3:24 PM
 */
public class IpUtil {

    /**
     * 获取客户端的IP
     * 正常情况通过getRemoteAddr()可以获得真实的IP，但是如果通过反向代理，那么获取到的就是代理服务器的IP
     * 这种情况需要从代理服务器中获取真实客户端的IP
     *
     * @param request
     * @return
     */
    public static String getIpAddrByRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
