package com.swp.springboot.controller.admin;

import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.MetaDto;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.service.IMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 描述:
 * 分类和标签控制器
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-31 2:59 PM
 */
@Controller
@RequestMapping("/admin/category")
public class CategoryController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Resource
    private IMetaService metaService;

    @RequestMapping("")
    public String index(HttpServletRequest request){
        List<MetaDto> categorys = metaService.getMetaList(Types.CATEGORY.getType(), null, WebConst.MAX_POST_NUMBER);
        List<MetaDto> tags = metaService.getMetaList(Types.TAG.getType(), null, WebConst.MAX_POST_NUMBER);
        request.setAttribute("categories",categorys);
        request.setAttribute("tags",tags);
        return "admin/category";
    }

    @RequestMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveCategory(@RequestParam String cname, @RequestParam Integer mid){

        try {
            metaService.saveMeta(Types.CATEGORY.getType(), cname, mid);
        } catch (Exception e) {
            String msg = "分类保存失败";
            return ExceptionHelper.handlerException(logger, msg, e);
        }

        return RestResponseBo.ok();
    }

    @RequestMapping("/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer mid) {

        try {
            metaService.delete(mid);
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "删除失败", e);
        }

        return RestResponseBo.ok();

    }


}
