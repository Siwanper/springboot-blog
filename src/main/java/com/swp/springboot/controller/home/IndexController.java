package com.swp.springboot.controller.home;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.MetaDto;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.ArchiveBo;
import com.swp.springboot.modal.bo.CommentBo;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.ContentVo;
import com.swp.springboot.modal.vo.MetaVo;
import com.swp.springboot.service.ICommentService;
import com.swp.springboot.service.IContentService;
import com.swp.springboot.service.IMetaService;
import com.swp.springboot.service.ISiteService;
import com.swp.springboot.util.IpUtil;
import com.swp.springboot.util.MyUtils;
import com.swp.springboot.util.PatternKit;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * 描述:
 * 首页控制
 *
 * @outhor ios
 * @create 2018-10-27 3:17 PM
 */
@Controller
public class IndexController extends AbstractController{

    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    @Resource
    private IContentService contentService;

    @Resource
    private ICommentService commentService;

    @Resource
    private IMetaService metaService;

    @Resource
    private ISiteService siteService;

    /**
     * 博客首页
     *
     * @param request
     * @param limit
     * @return
     */
    @GetMapping("/")
    public String index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "10") int limit){
        return index(request, 1, limit);
    }

    @GetMapping(value = "page/{page}")
    public String index(HttpServletRequest request, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "10") int limit) {
        // 开启thymeleaf缓存，加快访问速度
        page = page < 0 || page > WebConst.MAX_PAGE_NUMBER ? 1 : page;
        PageInfo<ContentVo> articleList = contentService.getArticleList(page, limit);
        List<MetaDto> metaList = metaService.getMetaList(Types.CATEGORY.getType(), null, WebConst.MAX_POST_NUMBER);
        request.setAttribute("articles", articleList);
        request.setAttribute("categories", metaList);
        if (page > 1) {
            this.title(request, "第"+page+"页");
        }
        return this.render("index");
    }

    /**
     * 退出登录
     *
     * @param response
     * @param session
     */
    public void logout(HttpServletResponse response, HttpSession session) {
        MyUtils.logout(session, response);
    }

    /**
     * 文章内容页
     *
     * @param request
     * @param cid
     * @return
     */
    @GetMapping(value = {"article/{cid}", "article/{cid}.html"})
    public String getArticle(HttpServletRequest request, @PathVariable String cid) {
        ContentVo contents = contentService.getContentByCid(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        compeleArticle(request, contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("page");
    }

    /**
     * 文章页（预览）
     *
     * @param request
     * @param cid
     * @return
     */
    @GetMapping(value = {"article/{cid}/preview", "article/{cid}.html"})
    public String articlePreview(HttpServletRequest request, @PathVariable String cid) {
        ContentVo content = contentService.getContentByCid(cid);
        if (null == content) {
            return this.render_404();
        }
        request.setAttribute("article", content);
        request.setAttribute("is_post", true);
        compeleArticle(request, content);
        updateArticleHit(content.getCid(), content.getHits());
        return this.render("post");
    }

    /**
     * 更新点击次数
     *
     * @param cid
     * @param chits
     */
    @Transactional(rollbackFor = TipException.class)
    protected void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article", "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateByCid(temp);
            cache.hset("article", "hits", 1);
        } else {
            cache.hset("article", "hits", 1);
        }
    }

    private void compeleArticle(HttpServletRequest request, ContentVo content) {
        if (content.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(content.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    /**
     * 评论操作
     * @param request
     * @param response
     * @param cid
     * @param coid
     * @param text
     * @param author
     * @param mail
     * @param url
     * @param _csrf_token
     * @return
     */
    @PostMapping("comment")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam Integer cid, @RequestParam Integer coid, @RequestParam String text,
                                  @RequestParam String author, @RequestParam String mail, @RequestParam String url,
                                  @RequestParam String _csrf_token) {
        String ref = request.getHeader("Referer");
        System.out.println("referer : " + ref);
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return RestResponseBo.fail("Bad request");
        }
        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
        if (StringUtils.isBlank(token)) {
            return RestResponseBo.fail("Bad request");
        }
        if ( null == cid || StringUtils.isBlank(text)) {
            return RestResponseBo.fail("请输入评论内容");
        }
        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return RestResponseBo.fail("姓名过长");
        }
        if (StringUtils.isNotBlank(mail) && !MyUtils.isEmail(mail)) {
            return RestResponseBo.fail("请输入正确的邮箱格式");
        }
        if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
            return RestResponseBo.fail("请输入正确的URL格式");
        }
        if (text.length() > 200) {
            return RestResponseBo.fail("请输入200个字符之内的评论内容");
        }

        String val = IpUtil.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return RestResponseBo.fail("请发表的评论太快了，请稍后尝试");
        }

        // 去除js脚本
        author = MyUtils.cleanXSS(author);
        text = MyUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        CommentVo comments = new CommentVo();
        comments.setAuthor(author);
        comments.setCid(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setMail(mail);
        comments.setParent(coid);
        comments.setContent(text);

        try {
            commentService.insertComment(comments);

            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLDecoder.decode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            }

            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1 , 60);
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "评论发布失败", e);
        }

        return RestResponseBo.ok();
    }

    /**
     * 分类页
     *
     * @param request
     * @param keyword
     * @param limit
     * @return
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.categories(request, keyword, 1, limit);
    }

    @RequestMapping("category/{keyword}/{page}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE_NUMBER ? 1 : page;
        MetaDto metaDto = metaService.getMeta(Types.CATEGORY.getType(), keyword);
        if (null == metaDto) {
            return this.render_404();
        }
        PageInfo<ContentVo> articleList = contentService.getArticleList(metaDto.getMid(), page, limit);
        request.setAttribute("articles", articleList);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 归档也
     * @param request
     * @return
     */
    @GetMapping("/archives")
    public String archives(HttpServletRequest request){
        List<ArchiveBo> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return this.render("archives");
    }

    /**
     * 友链页面
     * @param request
     * @return
     */
    @GetMapping("/links")
    public String links(HttpServletRequest request) {
        List<MetaVo> links = metaService.getMetaByType(Types.LINK.getType());
        request.setAttribute("links", links);
        return this.render("links");
    }

    /**
     * 搜索
     * @param request
     * @param keyword
     * @param limit
     * @return
     */
    @GetMapping("/search/{keyword}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.search(request,keyword, 1, limit);
    }

    @GetMapping("/search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable String keyword,@PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE_NUMBER ? 1 : page;
        PageInfo<ContentVo> articleList = contentService.getArticleList(keyword, page, limit);
        request.setAttribute("articles", articleList);
        request.setAttribute("type", "搜索");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 标签页面
     * @param request
     * @param name
     * @param limit
     * @return
     */
    @GetMapping("/tag/{name}")
    public String tags(HttpServletRequest request, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, name, 1, limit);
    }

    public String tags(HttpServletRequest request, @PathVariable String name, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE_NUMBER ? 1 : page;
        name = name.replaceAll("\\+", " ");
        MetaDto meta = metaService.getMeta(Types.TAG.getType(), name);
        if (null == meta) {
            return this.render_404();
        }
        PageInfo<ContentVo> articleList = contentService.getArticleList(meta.getMid(), page, limit);
        request.setAttribute("articles", articleList);
        request.setAttribute("meta", meta);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", name);
        return this.render("page-category");
    }

    /**
     * IP被禁后的页面
     *
     * @param request
     * @return
     */
    @GetMapping(value = "common/ipban")
    public String ipBan(HttpServletRequest request) {
        return "common/ipban";
    }


    @GetMapping(value = "/about")
    @ResponseBody
    public String about(){
        return "个人技术博客";
    }

}
