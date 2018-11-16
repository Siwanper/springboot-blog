package com.swp.springboot.controller.admin;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.CommentVoExample;
import com.swp.springboot.service.ICommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 描述:
 * 评论控制器
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-07 11:01 AM
 */
@Controller
@RequestMapping("admin/comments")
public class CommentController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Resource
    private ICommentService commentService;

    /**
     * 评论列表
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "10") int limit, HttpServletRequest request) {
        CommentVoExample example = new CommentVoExample();
        example.setOrderByClause("coid desc");
        example.createCriteria().andAuthorIdNotEqualTo(this.getUid(request));
        PageInfo<CommentVo> comments = commentService.getCommentsWithPage(example, page, limit);
        request.setAttribute("comments", comments);
        return "admin/comment_list";
    }

    /**
     * 删除评论
     * @param coid
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer coid){

        try {
            commentService.deleteCommentByCoid(coid);
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "删除评论失败", e);
        }

        return RestResponseBo.ok();
    }


}
