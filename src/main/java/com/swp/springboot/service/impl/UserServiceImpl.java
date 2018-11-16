package com.swp.springboot.service.impl;

import com.swp.springboot.dao.UserVoMapper;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.vo.UserVo;
import com.swp.springboot.modal.vo.UserVoExample;
import com.swp.springboot.service.IUserService;
import com.swp.springboot.util.MyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述:
 *
 * @outhor ios
 * @create 2018-10-25 4:48 PM
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserVoMapper mapper;

    @Override
    public UserVo login(String username, String password) {
        // 1、判断用户名和密码是否为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名或密码为空");
        }
        UserVoExample example = new UserVoExample();
        UserVoExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        // 2、判断用户是否存在
        long count = mapper.countByExample(example);
        if (count < 1) {
            throw new TipException("用户不存在");
        }
        // 3、判断密码是否正确
        String pwd = MyUtils.MD5encode(username + password);
        criteria.andPasswordEqualTo(pwd);
        List<UserVo> userVoList = mapper.selectByExample(example);
        if (userVoList.size() != 1){
            throw new TipException("密码错误");
        }
        return userVoList.get(0);
    }

    @Override
    public void updateByUid(UserVo userVo) {
        if (null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = mapper.updateByPrimaryKeySelective(userVo);
        if (i != 1) {
            throw new TipException("update user info failure");
        }
    }
}
