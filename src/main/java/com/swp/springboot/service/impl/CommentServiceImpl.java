package com.swp.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.swp.springboot.dao.CommentVoMapper;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.CommentBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.CommentVoExample;
import com.swp.springboot.modal.vo.ContentVo;
import com.swp.springboot.service.ICommentService;
import com.swp.springboot.service.IContentService;
import com.swp.springboot.util.DateKit;
import com.swp.springboot.util.MyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 文章评论的业务处理
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-01 5:00 PM
 */
@Service
public class CommentServiceImpl implements ICommentService {

    @Resource
    private CommentVoMapper commentDao;

    @Resource
    private IContentService contentService;

    @Override
    public PageInfo<CommentBo> getComments(Integer cid, int page, int limit) {
        if (null != cid) {
            PageHelper.startPage(page, limit);
            CommentVoExample commentVoExample = new CommentVoExample();
            commentVoExample.createCriteria().andCidEqualTo(cid).andParentEqualTo(0);
            commentVoExample.setOrderByClause("coid desc");
            List<CommentVo> parents = commentDao.selectByExampleWithBLOBs(commentVoExample);
            PageInfo<CommentVo> commentPaginator = new PageInfo<>(parents);
            PageInfo<CommentBo> returnBo = copyPageInfo(commentPaginator);
            if (parents.size() != 0) {
                List<CommentBo> comments = new ArrayList<>(parents.size());
                parents.forEach(parent -> {
                    CommentBo comment = new CommentBo(parent);
                    comments.add(comment);
                });
                returnBo.setList(comments);
            }
            return returnBo;
        }
        return null;
    }

    @Override
    public PageInfo<CommentVo> getCommentsWithPage(CommentVoExample example, int page, int limit) {
        PageHelper.startPage(page, limit);
        List<CommentVo> commentVoList = commentDao.selectByExampleWithBLOBs(example);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(commentVoList);
        return pageInfo;
    }

    @Override
    public void insertComment(CommentVo comments) {
        checkComment(comments);
        ContentVo content = contentService.getContentByCid(String.valueOf(comments.getCid()));
        if (null == content) {
            throw new TipException("不存在的文章");
        }
        comments.setOwnerId(content.getAuthorId());
        comments.setCreated(DateKit.getCurrentUnixTime());
        commentDao.insertSelective(comments);

        ContentVo temp = new ContentVo();
        temp.setCid(content.getCid());
        temp.setCommentsNum(content.getCommentsNum() + 1);
        contentService.updateByCid(temp);
    }

    @Override
    public void deleteCommentByCoid(Integer coid) {
        if (null == coid) {
            throw new TipException("评论不存在");
        }
        CommentVo commentVo = commentDao.selectByPrimaryKey(coid);
        if (null == commentVo) {
            throw new TipException("评论不存在");
        }
        Integer cid = commentVo.getCid();
        if (null == cid) {
            throw new TipException("评论的文章不存在");
        }
        ContentVo contentVo = contentService.getContentByCid(cid + "");
        if (null == contentVo) {
            throw new TipException("评论的文章不存在");
        }

        ContentVo temp = new ContentVo();
        temp.setCid(cid);
        Integer commentsNum = contentVo.getCommentsNum();
        if (commentsNum > 0) {
            temp.setCommentsNum(commentsNum-1);
        }
        contentService.updateByCid(temp);
        commentDao.deleteByPrimaryKey(coid);
    }

    private void checkComment(CommentVo comments) throws TipException {
        if (null == comments) {
            throw new TipException("评论对象为空");
        }
        if (StringUtils.isBlank(comments.getAuthor())) {
            comments.setAuthor("热心网友");
        }
        if (StringUtils.isNotBlank(comments.getMail()) && !MyUtils.isEmail(comments.getMail())) {
            throw new TipException("请输入正确的邮箱格式");
        }
        if (StringUtils.isBlank(comments.getContent())) {
            throw new TipException("评论内容不能为空");
        }
        if (comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
            throw new TipException("评论字数在5-2000个字符");
        }
        if (null == comments.getCid()) {
            throw new TipException("评论文章不能为空");
        }

    }

    /**
     * copy原有的分页信息，除数据
     *
     * @param ordinal
     * @param <T>
     * @return
     */
    private <T> PageInfo<T> copyPageInfo(PageInfo ordinal) {
        PageInfo<T> returnBo = new PageInfo<T>();
        returnBo.setPageSize(ordinal.getPageSize());
        returnBo.setPageNum(ordinal.getPageNum());
        returnBo.setEndRow(ordinal.getEndRow());
        returnBo.setTotal(ordinal.getTotal());
        returnBo.setHasNextPage(ordinal.isHasNextPage());
        returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
        returnBo.setIsFirstPage(ordinal.isIsFirstPage());
        returnBo.setIsLastPage(ordinal.isIsLastPage());
        returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
        returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
        returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
        returnBo.setSize(ordinal.getSize());
        returnBo.setPrePage(ordinal.getPrePage());
        returnBo.setNextPage(ordinal.getNextPage());
        return returnBo;
    }
}
