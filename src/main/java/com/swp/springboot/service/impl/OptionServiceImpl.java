package com.swp.springboot.service.impl;

import com.swp.springboot.dao.OptionVoMapper;
import com.swp.springboot.modal.vo.OptionVo;
import com.swp.springboot.modal.vo.OptionVoExample;
import com.swp.springboot.service.IOptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 设置选项
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-09 4:52 PM
 */
@Service
public class OptionServiceImpl implements IOptionService {

    @Resource
    private OptionVoMapper optionVoMapper;

    @Override
    public List<OptionVo> getOptions() {
        return optionVoMapper.selectByExample(new OptionVoExample());
    }

    @Override
    public void insertOption(String name, String value) {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            OptionVo optionVo = optionVoMapper.selectByPrimaryKey(name);
            if (null == optionVo) {
                optionVo = new OptionVo();
                optionVo.setName(name);
                optionVo.setValue(value);
                optionVoMapper.insertSelective(optionVo);
            } else {
                optionVo.setValue(value);
                optionVoMapper.updateByPrimaryKeySelective(optionVo);
            }
        }
    }

    @Override
    public void saveOption(Map<String, String> query) {
        if (null != query && !query.isEmpty()) {
            query.forEach(this::insertOption);
        }
    }
}
