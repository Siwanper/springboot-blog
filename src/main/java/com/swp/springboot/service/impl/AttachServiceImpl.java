package com.swp.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.swp.springboot.constant.WebConst;
import com.swp.springboot.dao.AttachVoMapper;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.vo.AttachVo;
import com.swp.springboot.modal.vo.AttachVoExample;
import com.swp.springboot.service.IAttachService;
import com.swp.springboot.util.DateKit;
import com.swp.springboot.util.MyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 描述:
 * 附件上传服务
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-03 10:11 AM
 */
@Service
public class AttachServiceImpl implements IAttachService {

    @Resource
    private AttachVoMapper mapper;

    @Override
    public void save(String filename, String fkey, String ftype, Integer uid) {
        AttachVo attachVo = new AttachVo();
        attachVo.setAuthorId(uid);
        attachVo.setCreated(DateKit.getCurrentUnixTime());
        attachVo.setFkey(fkey);
        attachVo.setFtype(ftype);
        attachVo.setFname(filename);
        mapper.insert(attachVo);
    }

    @Override
    public PageInfo<AttachVo> getAttachList(int page, int limit) {
        page = page < 0 ? 1 : page;
        limit = limit < 0 || limit > WebConst.MAX_POST_NUMBER ? 12 : limit;
        AttachVoExample example = new AttachVoExample();
        example.setOrderByClause("id desc");
        PageHelper.startPage(page, limit);
        List<AttachVo> attachVos = mapper.selectByExample(example);
        PageInfo<AttachVo> pageInfo = new PageInfo<>(attachVos);
        return pageInfo;
    }

    @Override
    public AttachVo getAttachById(Integer id) {
        if (null != id) {
            AttachVo attachVo = mapper.selectByPrimaryKey(id);
            return attachVo;
        }
        return null;
    }

    @Override
    public void deleteAttachById(Integer id) {
        AttachVo attachVo = this.getAttachById(id);
        if (null == attachVo) {
            throw new TipException("该文件不存在");
        }
        int i = mapper.deleteByPrimaryKey(id);
        if (i == 1) {
            new File(MyUtils.getUploadFilePath() + attachVo.getFkey()).delete();
        }else {
            throw new TipException("删除失败");
        }

    }
}
