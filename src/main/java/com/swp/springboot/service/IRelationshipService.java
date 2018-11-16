package com.swp.springboot.service;

import com.swp.springboot.modal.vo.RelationshipVoKey;

import java.util.List;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.service
 * @Author: Siwanper
 * @CreateDate: 2018/10/31 下午9:41
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
public interface IRelationshipService {

    /**
     * 根据mid获取关联列表
     * 
     * @param mid
     * @return
     */
    List<RelationshipVoKey> getRelationshioListById(Integer cid, Integer mid);

    /**
     * 删除关联
     *
     * @param cid
     * @param mid
     */
    void deleteById(Integer cid, Integer mid);

    Long countById(Integer cid, Integer mid);

    void insertVo(RelationshipVoKey relationshipVoKey);
}
