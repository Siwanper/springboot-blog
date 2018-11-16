package com.swp.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.swp.springboot.constant.WebConst;
import com.swp.springboot.dao.LogVoMapper;
import com.swp.springboot.modal.vo.LogVo;
import com.swp.springboot.modal.vo.LogVoExample;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.util.DateKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 * log业务实现
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-26 10:58 AM
 */
@Service
public class LogServiceImpl implements ILogService {

    @Resource
    private LogVoMapper mapper;

    @Override
    public void insertLog(String action, String data, Integer authorId, String ip) {
        LogVo logVo = new LogVo();
        logVo.setAction(action);
        logVo.setData(data);
        logVo.setAuthorId(authorId);
        logVo.setIp(ip);
        logVo.setCreated(DateKit.getCurrentUnixTime());
        mapper.insert(logVo);
    }

    @Override
    public List<LogVo> getLogs(int page, int limit) {
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POST_NUMBER) {
            limit = 10;
        }
        LogVoExample example = new LogVoExample();
        PageHelper.startPage((page - 1) * limit, limit);
        List<LogVo> logVos = mapper.selectByExample(example);
        return logVos;
    }
}
