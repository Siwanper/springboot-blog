package com.swp.springboot.service;

import com.swp.springboot.modal.vo.UserVo;

/**
 * 用户业务处理类
 */
public interface IUserService {

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    UserVo login(String username, String password);

    /**
     * 更新用户
     *
     * @param userVo
     */
    void updateByUid(UserVo userVo);


}
