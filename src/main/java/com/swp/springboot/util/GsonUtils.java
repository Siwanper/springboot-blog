package com.swp.springboot.util;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.json.GsonJsonParser;

/**
 * 描述:
 * Gson转换工具
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-30 3:17 PM
 */
public class GsonUtils {
    private static final Gson gson = new Gson();

    /**
     * 将对象转换成json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return object == null ? null : gson.toJson(object);
    }

    /**
     * 将json字符串转换成指定类型对象
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static Object fromJson(String jsonStr, Class clazz) {
        Object object = null;
        if (StringUtils.isNotBlank(jsonStr)) {
            object = gson.fromJson(jsonStr, clazz);
        }
        return object;
    }


}
