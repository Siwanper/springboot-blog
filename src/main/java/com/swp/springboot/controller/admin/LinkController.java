package com.swp.springboot.controller.admin;

import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.MetaVo;
import com.swp.springboot.service.IMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 描述:
 * 友链接
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-09 3:42 PM
 */
@Controller()
@RequestMapping("admin/links")
public class LinkController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LinkController.class);

    @Resource
    private IMetaService metaService;

    @GetMapping("")
    public String index(HttpServletRequest request){
        List<MetaVo> metaVos = metaService.getMetaByType(Types.LINK.getType());
        request.setAttribute("links", metaVos);
        return "admin/links";
    }

    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo save(HttpServletRequest request, @RequestParam Integer mid,
                               @RequestParam String title, @RequestParam String url,
                               @RequestParam String logo, @RequestParam int sort){

        try {
            MetaVo metaVo = new MetaVo();
            metaVo.setName(title);
            metaVo.setSlug(url);
            metaVo.setDescription(logo);
            metaVo.setSort(sort);
            metaVo.setType(Types.LINK.getType());
            if (null != mid) {
                metaVo.setMid(mid);
                metaService.updateMeta(metaVo);
            }else {
                metaService.saveMeta(metaVo);
            }
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "保存链接失败", e);
        }

        return RestResponseBo.ok();
    }

    @PostMapping("delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer mid) {
        try {
            metaService.delete(mid);
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "删除链接失败", e);
        }
        return RestResponseBo.ok();
    }

}
