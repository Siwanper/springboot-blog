package com.swp.springboot.service;

import com.swp.springboot.modal.vo.LogVo;

import java.util.List;

public interface ILogService {

    /**
     * 保存log
     *
     * @param action
     * @param data
     * @param authorId
     * @param ip
     */
    public void insertLog(String action, String data, Integer authorId, String ip);

    /**
     * 获取日志列表
     *
     * @param page 页数
     * @param limit 限制
     * @return
     */
    public List<LogVo> getLogs(int page, int limit);


}
