package com.swp.springboot.service;

import com.swp.springboot.modal.vo.OptionVo;

import java.util.List;
import java.util.Map;

public interface IOptionService {

    public List<OptionVo> getOptions();

    void saveOption(Map<String, String> query);

    public void insertOption(String name, String value);

}
