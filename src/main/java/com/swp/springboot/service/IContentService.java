package com.swp.springboot.service;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.modal.vo.ContentVo;

import java.util.List;

public interface IContentService {

    /**
     * 获取文章列表
     *
     * @param page
     * @param limit
     * @return
     */
    PageInfo<ContentVo> getArticleList(int page, int limit);

    /**
     * 根据分类获取文章
     *
     * @param mid
     * @param page
     * @param limit
     * @return
     */
    PageInfo<ContentVo> getArticleList(Integer mid, int page, int limit);

    /**
     * 搜索文章
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    PageInfo<ContentVo> getArticleList(String keyword, int page, int limit);

    /**
     * 获取文章
     *
     * @param cid
     * @return
     */
    ContentVo getContentByCid(String cid);

    /**
     * 更新文章的类别和标签
     *
     * @param oldName
     * @param newName
     */
    void updateCategory(String oldName, String newName);

    /**
     * 更新文章
     *
     * @param contentVo
     */
    void updateByCid(ContentVo contentVo);

    /**
     * 发表文章
     * @param contentVo
     */
    void publish(ContentVo contentVo);

    /**
     * 更新文章
     * @param contents
     */
    void updateArticle(ContentVo contents);

    /**
     * 删除文章
     * @param cid
     */
    void deleteArticleById(Integer cid);
}
