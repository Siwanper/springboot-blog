package com.swp.springboot.controller.admin;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.LogActions;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.ContentVo;
import com.swp.springboot.modal.vo.ContentVoExample;
import com.swp.springboot.modal.vo.MetaVo;
import com.swp.springboot.modal.vo.UserVo;
import com.swp.springboot.service.IContentService;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.service.IMetaService;
import com.swp.springboot.util.Commons;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.controller.admin
 * @Author: Siwanper
 * @CreateDate: 2018/10/31 下午10:56
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
@Controller
@RequestMapping("/admin/article")
public class ArticleController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private IContentService contentService;

    @Resource
    private IMetaService metaService;

    @Resource
    private ILogService logService;
    /**
     * 文章列表页面
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("")
    public String index(@RequestParam(value = "page" , defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit,
                        HttpServletRequest request){
        PageInfo<ContentVo> articleList = contentService.getArticleList(page, limit);
        request.setAttribute("articles", articleList);
        return "admin/article_list";
    }

    /**
     * 发布文章
     *
     * @return
     */
    @GetMapping("/publish")
    public String newArticle(HttpServletRequest request){

        List<MetaVo> metaVos = metaService.getMetaByType(Types.CATEGORY.getType());
        request.setAttribute("categories", metaVos);
        request.setAttribute(Types.ATTACH_URL.getType(), Commons.site_option(Types.ATTACH_URL.getType()));

        return "admin/article_edit";
    }

    /**
     * 发表文章
     *
     * @param contents
     * @param request
     * @return
     */
    @PostMapping("/publish")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo publishArticle(ContentVo contents, HttpServletRequest request){
        UserVo user = this.user(request);
        contents.setAuthorId(user.getUid());
        contents.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contents.getCategories())){
            contents.setCategories("默认分类");
        }
        try {
            contentService.publish(contents);
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "文章发布失败", e);
        }
        return RestResponseBo.ok();
    }

    /**
     * 文章编辑页面
     * @param request
     * @param cid
     * @return
     */
    @RequestMapping(value = "/{cid}")
    @Transactional(rollbackFor = TipException.class)
    public String editArticle(HttpServletRequest request,@PathVariable String cid) {
        ContentVo content = contentService.getContentByCid(cid);
        request.setAttribute("contents",content);
        List<MetaVo> metaVos = metaService.getMetaByType(Types.CATEGORY.getType());
        request.setAttribute("categories", metaVos);
        request.setAttribute(Types.ATTACH_URL.getType(), Commons.site_option(Types.ATTACH_URL.getType()));
        request.setAttribute("active","article");
        return "admin/article_edit";
    }

    /**
     * 编辑文章
     * @param request
     * @param contents
     * @return
     */
    @PostMapping("/modify")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo modifyArticle(HttpServletRequest request ,ContentVo contents){
        UserVo user = this.user(request);
        contents.setAuthorId(user.getUid());
        contents.setType(Types.ARTICLE.getType());
        contentService.updateArticle(contents);
        return RestResponseBo.ok();
    }

    @PostMapping("/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo deleteArticle(@RequestParam Integer cid, HttpServletRequest request) {

        try {
            contentService.deleteArticleById(cid);
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", this.getUid(request), request.getRemoteAddr());
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "文章删除失败", e);
        }

        return RestResponseBo.ok();
    }


}
