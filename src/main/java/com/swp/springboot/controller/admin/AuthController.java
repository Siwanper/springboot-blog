package com.swp.springboot.controller.admin;

import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.LogActions;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.UserVo;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.service.IUserService;
import com.swp.springboot.util.MyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 描述:
 * 登录控制器
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-25 2:55 PM
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private IUserService userService;

    @Resource
    private ILogService logService;

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public RestResponseBo doLogin(@RequestParam String username,
                                  @RequestParam String password,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        Integer errorCount = cache.get(WebConst.LOGIN_ERROR_COUNT);

        try {
            UserVo userVo = userService.login(username, password);
            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY,userVo);
            // 设置12小时的cookie
            MyUtils.setCookie(response,userVo.getUid());
            logService.insertLog(LogActions.LOGIN.getAction(),null,userVo.getUid(),request.getRemoteAddr());
        } catch (Exception e) {
            errorCount = null == errorCount ? 1 : errorCount + 1;
            if (errorCount > 3) {
                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
            }
            cache.set(WebConst.LOGIN_ERROR_COUNT,errorCount,10 * 60);
            String msg = "登录失败";
            return ExceptionHelper.handlerException(logger, msg, e);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("注销失败", e);
        }
    }


}
