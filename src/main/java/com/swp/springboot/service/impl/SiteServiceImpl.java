package com.swp.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.swp.springboot.dao.AttachVoMapper;
import com.swp.springboot.dao.CommentVoMapper;
import com.swp.springboot.dao.ContentVoMapper;
import com.swp.springboot.dao.MetaVoMapper;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.ArchiveBo;
import com.swp.springboot.modal.bo.BackupResponseBo;
import com.swp.springboot.modal.bo.StaticticsBo;
import com.swp.springboot.modal.vo.*;
import com.swp.springboot.service.ISiteService;
import com.swp.springboot.util.DateKit;
import com.swp.springboot.util.MyUtils;
import com.swp.springboot.util.ZipUtils;
import com.swp.springboot.util.backup.Backup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 描述:
 * 网站业务处理
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-27 4:55 PM
 */
@Service
public class SiteServiceImpl implements ISiteService {

    @Resource
    private CommentVoMapper commentVoDao;

    @Resource
    private ContentVoMapper contentVoDao;

    @Resource
    private AttachVoMapper attachVoDao;

    @Resource
    private MetaVoMapper metaVoDao;

    @Override
    public List<CommentVo> recentComments(int limit) {
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        CommentVoExample commentVoExample = new CommentVoExample();
        commentVoExample.setOrderByClause("created desc");
        PageHelper.startPage(1,limit);
        List<CommentVo> commentVoList = commentVoDao.selectByExampleWithBLOBs(commentVoExample);
        return commentVoList;
    }

    @Override
    public List<ContentVo> recentContents(int limit) {
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        ContentVoExample example = new ContentVoExample();
        example.createCriteria().andStatusEqualTo(Types.PUBLISH.getType())
                .andTypeEqualTo(Types.ARTICLE.getType());
        PageHelper.startPage(1,limit);
        List<ContentVo> contentVos = contentVoDao.selectByExampleWithBLOBs(example);
        return contentVos;
    }

    @Override
    public CommentVo getCommont(Integer coid) {
        if (null != coid) {
            commentVoDao.selectByPrimaryKey(coid);
        }
        return null;
    }

    @Override
    public StaticticsBo getStatictics() {
        StaticticsBo staticticsBo = new StaticticsBo();

        ContentVoExample example = new ContentVoExample();
        example.createCriteria().andStatusEqualTo(Types.PUBLISH.getType())
                .andTypeEqualTo(Types.ARTICLE.getType());
        Long articles = contentVoDao.countByExample(example);
        Long comments = commentVoDao.countByExample(new CommentVoExample());
        Long attachs = attachVoDao.countByExample(new AttachVoExample());

        MetaVoExample metaVoExample = new MetaVoExample();
        metaVoExample.createCriteria().andTypeEqualTo(Types.LINK.getType());
        Long links = metaVoDao.countByExample(metaVoExample);

        staticticsBo.setArticles(articles);
        staticticsBo.setComments(comments);
        staticticsBo.setAttachs(attachs);
        staticticsBo.setLinks(links);
        return staticticsBo;
    }

    @Override
    public List<ArchiveBo> getArchives() {
        List<ArchiveBo> archiveBos = contentVoDao.findRetureArchivesBo();
        if (null != archiveBos) {
            archiveBos.forEach(archiveBo -> {
                ContentVoExample example = new ContentVoExample();
                ContentVoExample.Criteria criteria = example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andStatusEqualTo(Types.PUBLISH.getType());
                String date = archiveBo.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int startDate = DateKit.getUnixTimeByDate(sd);
                int endDate = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                criteria.andCreatedGreaterThan(startDate);
                criteria.andCreatedLessThanOrEqualTo(endDate);
                List<ContentVo> contentVoList = contentVoDao.selectByExample(example);
                archiveBo.setArticles(contentVoList);
            });
        }
        return archiveBos;
    }

    @Override
    public BackupResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackupResponseBo backupResponseBo = new BackupResponseBo();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if (!((new File(bk_path)).isDirectory())) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = MyUtils.getUploadFilePath() + "upload";
            String bkThemesDir = MyUtils.getUploadFilePath() + "templates/themes";

            String filename = DateKit.dateFormat(new Date(), fmt) + "_" + MyUtils.getRandomNumber(5) +".zip";

            String attachPath = bk_path + "/attachs_" +filename;
            String themePath = bk_path + "/themes_" + filename;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themePath);

            backupResponseBo.setAttachPath(attachPath);
            backupResponseBo.setThemePath(themePath);
        }
        if (bk_type.equals("db")) {
            String bkAttachDir = MyUtils.getUploadFilePath() + "upload/";
            if (!(new File(bkAttachDir).isDirectory())) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + MyUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(MyUtils.getNewDataSource().getConnection());
            String sqlContent = backup.execute();
            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);

            Thread.sleep(500);
            if (!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();
            backupResponseBo.setSqlPath(zipFile);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }


        return null;
    }

    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch (IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }
    }


}
