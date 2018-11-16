package com.swp.springboot.config;

import com.swp.springboot.interception.BaseInterceptor;
import com.swp.springboot.util.MyUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * 描述:
 * webMvc 相关配置
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-26 4:16 PM
 */
@Component
public class WebMvcConfig extends WebMvcConfigurerAdapter{
    @Resource
    private BaseInterceptor baseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+ MyUtils.getUploadFilePath()+"upload/");
        super.addResourceHandlers(registry);
    }
}
