package com.swp.springboot.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 描述:
 * 常量
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 2:25 PM
 */
public class WebConst {

    public static Map<String, String> initConfig = new HashMap<>();

    public static final String LOGIN_SESSION_KEY = "login_user";
    public static final String LOGIN_ERROR_COUNT = "login_error_count";

    public static final String AES_SALT = "0123456789abcdef";
    public static final String USER_IN_COOKIE = "S_L_ID";

    /**
     * 点击次数超过多少更新到数据库
     */
    public static final int HIT_EXCEED = 10;
    /**
     * 最大获取文章条数
     */
    public static final int MAX_POST_NUMBER = 9999;

    /**
     * 最大页码
     */
    public static final int MAX_PAGE_NUMBER = 100;
    /**
     * 文章最多可以输入的文字数
     */
    public static final int MAX_TEXT_COUNT = 200000;

    /**
     * 上传文件最大1M
     */
    public static Integer MAX_FILE_SIZE = 1048576;

    /**
     * 要过滤的ip列表
     */
    public static Set<String> BLOCK_IPS = new HashSet<>(16);


}
