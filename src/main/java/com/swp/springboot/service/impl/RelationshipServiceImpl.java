package com.swp.springboot.service.impl;

import com.swp.springboot.dao.RelationshipVoMapper;
import com.swp.springboot.modal.vo.RelationshipVoExample;
import com.swp.springboot.modal.vo.RelationshipVoKey;
import com.swp.springboot.service.IRelationshipService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.service.impl
 * @Author: Siwanper
 * @CreateDate: 2018/10/31 下午9:43
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
@Service
public class RelationshipServiceImpl implements IRelationshipService {

    @Resource
    private RelationshipVoMapper mapper;

    @Override
    public List<RelationshipVoKey> getRelationshioListById(Integer cid, Integer mid) {
        RelationshipVoExample example = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = example.createCriteria();
        if (null != mid) {
            criteria.andMidEqualTo(mid);
        }
        if (null != cid) {
            criteria.andCidEqualTo(cid);
        }
        List<RelationshipVoKey> relationshipVoKeys = mapper.selectByExample(example);
        return relationshipVoKeys;
    }

    @Override
    public void deleteById(Integer cid, Integer mid) {
        RelationshipVoExample example = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = example.createCriteria();
        if (null != mid) {
            criteria.andMidEqualTo(mid);
        }
        if (null != cid) {
            criteria.andCidEqualTo(cid);
        }
        mapper.deleteByExample(example);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        RelationshipVoExample example = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = example.createCriteria();
        if (null != cid) {
            criteria.andCidEqualTo(cid);
        }
        if (null != mid) {
            criteria.andCidEqualTo(mid);
        }
        long count = mapper.countByExample(example);
        return count;
    }

    @Override
    public void insertVo(RelationshipVoKey relationshipVoKey) {
        if ( null != relationshipVoKey) {
            mapper.insert(relationshipVoKey);
        }
    }
}
