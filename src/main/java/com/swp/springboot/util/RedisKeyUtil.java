package com.swp.springboot.util;

/**
 * 描述:
 * redis主键获取
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-06 3:18 PM
 */
public class RedisKeyUtil {

    /**
     * redis的key
     * 形式为：
     * 表名:主键名:主键值:列名
     *
     * @param tableName 表名
     * @param majorKey 主键名
     * @param majorKeyValue 主键值
     * @param column 列名
     * @return
     */
    public static String getKeyWithColumn(String tableName,String majorKey,String majorKeyValue,String column){
        StringBuffer buffer = new StringBuffer();
        buffer.append(tableName).append(":");
        buffer.append(majorKey).append(":");
        buffer.append(majorKeyValue).append(":");
        buffer.append(column);
        return buffer.toString();
    }

    /**
     * redis 的 key
     * 形式：
     * 表名:主键名:主键值
     * @param tableName
     * @param majorKey
     * @param majorKeyValue
     * @return
     */
    public static String getKey(String tableName, String majorKey, String majorKeyValue) {
        StringBuffer sb = new StringBuffer();
        sb.append(tableName).append(":");
        sb.append(majorKey).append(":");
        sb.append(majorKeyValue);
        return sb.toString();
    }

}
