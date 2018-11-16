package com.swp.springboot.controller;

import com.swp.springboot.modal.vo.UserVo;
import com.swp.springboot.util.MapCache;
import com.swp.springboot.util.MyUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述:
 * 抽象基础类
 * 用于统一渲染页面URL，页面名称，获取session中的用户
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 10:01 AM
 */
public abstract class AbstractController {

    public static String THEME = "themes/siwanper";

    protected MapCache cache = MapCache.single();

    /**
     * 主页的页面主题
     *
     * @param viewName
     * @return
     */
    public String render(String viewName) {
        return THEME + "/" + viewName;
    }

    /**
     * 错误页面
     *
     * @return
     */
    public String render_404(){
        return "common/error_404";
    }

    /**
     * 页面名称
     *
     * @param request
     * @param title
     * @return
     */
    public AbstractController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    /**
     * 页面关键字
     *
     * @param request
     * @param keywords
     * @return
     */
    public AbstractController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }

    /**
     * 登录的用户
     *
     * @param request
     * @return
     */
    public UserVo user(HttpServletRequest request) {
        UserVo loginUser = MyUtils.getLoginUser(request);
        return loginUser;
    }

    /**
     * 获取登录用户的uid
     *
     * @param request
     * @return
     */
    public Integer getUid(HttpServletRequest request) {
        return this.user(request).getUid();
    }

}
