package com.swp.springboot.controller.admin;

import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.LogActions;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.bo.StaticticsBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.ContentVo;
import com.swp.springboot.modal.vo.LogVo;
import com.swp.springboot.modal.vo.UserVo;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.service.ISiteService;
import com.swp.springboot.service.IUserService;
import com.swp.springboot.util.GsonUtils;
import com.swp.springboot.util.MyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 描述:
 * 后台管理首页
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 4:34 PM
 */
@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private ISiteService siteService;

    @Resource
    private ILogService logService;

    @Resource
    private IUserService userService;

    /**
     * 后台管理中心首页
     *
     * @param request
     * @return
     */
    @GetMapping(value = {"", "/index"})
    public String index(HttpServletRequest request){

        List<CommentVo> commentVos = siteService.recentComments(5);
        List<ContentVo> contentVos = siteService.recentContents(5);
        StaticticsBo statictics = siteService.getStatictics();

        request.setAttribute("comments", commentVos);
        request.setAttribute("articles", contentVos);
        request.setAttribute("statistics", statictics);

        List<LogVo> logs = logService.getLogs(1, 5);
        request.setAttribute("logs",logs);

        return "admin/index";
    }

    /**
     * 个人设置页面
     *
     * @return
     */
    @GetMapping(value = "/profile")
    public String profile(){
        return "/admin/profile";
    }

    /**
     * 保存个人信息
     *
     * @param screenName
     * @param email
     * @param request
     * @return
     */
    @PostMapping(value = "/profile")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveProfile(@RequestParam String screenName, @RequestParam String email, HttpServletRequest request, HttpSession session){
        UserVo userVo = this.user(request);
        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            UserVo tempVo = new UserVo();
            tempVo.setUid(userVo.getUid());
            tempVo.setScreenName(screenName);
            tempVo.setEmail(email);
            userService.updateByUid(tempVo);
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJson(tempVo), this.getUid(request), request.getRemoteAddr());

            // 更新session中的数据
            UserVo originalUser = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            originalUser.setEmail(email);
            originalUser.setScreenName(screenName);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, originalUser);

        }
        return RestResponseBo.ok();
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/password")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo password(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request, HttpSession session) {
        UserVo user = this.user(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return RestResponseBo.fail("请输入完整信息！");
        }
        if (!user.getPassword().equals(MyUtils.MD5encode(user.getUsername() + oldPassword))) {
            return RestResponseBo.fail("原始密码不正确！");
        }
        if (newPassword.length() < 6 || newPassword.length() > 14) {
            return RestResponseBo.fail("请输入6到14位密码！");
        }

        try {
            UserVo tempVo = new UserVo();
            tempVo.setUid(user.getUid());
            String md5Pwd = MyUtils.MD5encode(user.getUsername() + newPassword);
            tempVo.setPassword(md5Pwd);
            userService.updateByUid(tempVo);
            logService.insertLog(LogActions.UP_PWD.getAction(), GsonUtils.toJson(tempVo), this.getUid(request), request.getRemoteAddr());

            // 更新session中的数据
            UserVo originalUser = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            originalUser.setPassword(md5Pwd);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, originalUser);
            return RestResponseBo.ok();
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "密码修改失败", e);
        }
    }


}
