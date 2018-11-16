package com.swp.springboot.modal.bo;

import com.swp.springboot.modal.vo.ContentVo;

import java.util.List;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.modal.bo
 * @Author: Siwanper
 * @CreateDate: 2018/11/14 下午10:42
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
public class ArchiveBo {

    private String date;
    private String count;
    private List<ContentVo> articles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<ContentVo> getArticles() {
        return articles;
    }

    public void setArticles(List<ContentVo> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "ArchiveBo{" +
                "date='" + date + '\'' +
                ", count='" + count + '\'' +
                ", articles=" + articles +
                '}';
    }
}
