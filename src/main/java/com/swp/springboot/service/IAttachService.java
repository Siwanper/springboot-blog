package com.swp.springboot.service;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.modal.vo.AttachVo;

public interface IAttachService {

    /**
     * 保存附件
     * @param filename
     * @param fkey
     * @param ftype
     * @param uid
     */
    void save(String filename, String fkey, String ftype, Integer uid);

    /**
     * 获取附件
     * @param page
     * @param limit
     * @return
     */
    PageInfo<AttachVo> getAttachList(int page, int limit);

    /**
     * 根据id获取附件
     * @param id
     * @return
     */
    AttachVo getAttachById(Integer id);

    /**
     * 删除附件
     * @param id
     */
    void deleteAttachById(Integer id);
}
