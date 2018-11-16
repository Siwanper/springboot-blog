package com.swp.springboot.util;

import com.swp.springboot.modal.vo.MetaVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 管理员
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-30 11:23 AM
 */
@Component
public class AdminCommons {

    /**
     * 判断metaVo 是否 和文章中的category有交集
     * @param metaVo
     * @param category
     * @return
     */
    public static boolean exist_category(MetaVo metaVo, String category){
        String[] categroys = StringUtils.split(category, ",");
        for (String ct : categroys) {
            if (ct.trim().equals(metaVo.getName())) {
                return true;
            }
        }
        return false;
    }

    private static final String[] COLORS = {"jantent", "primary", "success", "info", "warning", "danger", "inverse", "purple", "pink"};

    public static String rand_color() {
        int r = Tools.rand(0, COLORS.length - 1);
        return COLORS[r];
    }
}
