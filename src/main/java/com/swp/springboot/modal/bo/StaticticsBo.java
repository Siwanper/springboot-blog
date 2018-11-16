package com.swp.springboot.modal.bo;

import java.io.Serializable;

/**
 * 描述:
 * 后台统计数据
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-27 4:51 PM
 */
public class StaticticsBo implements Serializable {

    private Long articles;
    private Long comments;
    private Long links;
    private Long attachs; // 上传的附件个数

    public Long getArticles() {
        return articles;
    }

    public void setArticles(Long articles) {
        this.articles = articles;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getLinks() {
        return links;
    }

    public void setLinks(Long links) {
        this.links = links;
    }

    public Long getAttachs() {
        return attachs;
    }

    public void setAttachs(Long attachs) {
        this.attachs = attachs;
    }

    @Override
    public String toString() {
        return "StaticticsBo{" +
                "articles=" + articles +
                ", comments=" + comments +
                ", links=" + links +
                ", attachs=" + attachs +
                '}';
    }
}
