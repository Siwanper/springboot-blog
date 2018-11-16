package com.swp.springboot.service;

import com.swp.springboot.modal.bo.ArchiveBo;
import com.swp.springboot.modal.bo.BackupResponseBo;
import com.swp.springboot.modal.bo.StaticticsBo;
import com.swp.springboot.modal.vo.CommentVo;
import com.swp.springboot.modal.vo.ContentVo;

import java.util.List;

public interface ISiteService {

    /**
     * 获取最新的评论
     *
     * @return
     */
    public List<CommentVo> recentComments(int limit);

    /**
     * 获取最新的文章
     *
     * @param limit
     * @return
     */
    public List<ContentVo> recentContents(int limit);

    /**
     * 获取一条评论
     * @param coid
     * @return
     */
    public CommentVo getCommont(Integer coid);

    /**
     * 获取后台统计数据
     *
     * @return
     */
    public StaticticsBo getStatictics();

    /**
     * 获取文章归档
     * @return
     */
    List<ArchiveBo> getArchives();

    /**
     * 系统备份
     * @param bk_type
     * @param bk_path
     * @param fmt
     * @return
     */
    public BackupResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception;


}
