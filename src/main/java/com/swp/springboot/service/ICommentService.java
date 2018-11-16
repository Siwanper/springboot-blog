package com.swp.springboot.service;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.modal.bo.CommentBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.CommentVoExample;

public interface ICommentService {

    /**
     * 获取文章的评论
     * @param cid
     * @param page
     * @param limit
     * @return
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取评论列表
     * @param example
     * @param page
     * @param limit
     * @return
     */
    PageInfo<CommentVo> getCommentsWithPage(CommentVoExample example, int page, int limit);

    /**
     * 添加评论
     * @param comments
     */
    void insertComment(CommentVo comments);

    /**
     * 删除评论
     * @param coid
     */
    void deleteCommentByCoid(Integer coid);
}
