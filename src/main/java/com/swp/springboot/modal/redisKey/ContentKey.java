package com.swp.springboot.modal.redisKey;

/**
 * 描述:
 * redis缓存字段
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-06 3:14 PM
 */
public class ContentKey {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "t_contents";
    /**
     * 主键名
     */
    public static final String MAJOR_KEY = "cid";
    // 默认主键值
    public static final String DEFAULT_VALUE = "all";
    /**
     * 生成周期
     */
    public static final int LIVE_TIME = 6;

}
