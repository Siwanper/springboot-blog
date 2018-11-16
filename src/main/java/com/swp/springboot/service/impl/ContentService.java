package com.swp.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.swp.springboot.dao.ContentVoMapper;
import com.swp.springboot.dao.MetaVoMapper;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.redisKey.ContentKey;
import com.swp.springboot.modal.vo.ContentVo;
import com.swp.springboot.modal.vo.ContentVoExample;
import com.swp.springboot.service.IContentService;
import com.swp.springboot.service.IMetaService;
import com.swp.springboot.service.IRelationshipService;
import com.swp.springboot.util.DateKit;
import com.swp.springboot.util.MyUtils;
import com.swp.springboot.util.RedisKeyUtil;
import com.swp.springboot.util.Tools;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 文章业务处理
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-31 5:37 PM
 */
@Service
public class ContentService implements IContentService {

    @Resource
    private ContentVoMapper contentVoMapper;

    @Resource
    private IMetaService metaService;

    @Resource
    private MetaVoMapper metaVoMapper;

    @Resource
    private IRelationshipService relationshipService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisService redisService;

    @Override
    public PageInfo<ContentVo> getArticleList(int page, int limit) {

        ContentVoExample example = new ContentVoExample();
        example.setOrderByClause("created desc");
        ContentVoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(Types.ARTICLE.getType());
        PageHelper.startPage(page, limit);
        List<ContentVo> contentVos = contentVoMapper.selectByExampleWithBLOBs(example);

        return new PageInfo<>(contentVos);
    }

    @Override
    public PageInfo<ContentVo> getArticleList(Integer mid, int page, int limit) {
        int total = metaVoMapper.countWithSql(mid);
        PageHelper.startPage(page, limit);
        List<ContentVo> list = contentVoMapper.findByCatelog(mid);
        PageInfo<ContentVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public PageInfo<ContentVo> getArticleList(String keyword, int page, int limit) {
        ContentVoExample example = new ContentVoExample();
        example.setOrderByClause("created desc");
        ContentVoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(Types.ARTICLE.getType());
        criteria.andStatusEqualTo(Types.PUBLISH.getType());
        criteria.andTitleLike("%" + keyword + "%");
        PageHelper.startPage(page, limit);
        List<ContentVo> contentVos = contentVoMapper.selectByExampleWithBLOBs(example);
        PageInfo<ContentVo> pageInfo = new PageInfo<>(contentVos);
        return pageInfo;
    }

    @Override
    public ContentVo getContentByCid(String cid) {
        String contentKey = RedisKeyUtil.getKey(ContentKey.TABLE_NAME, ContentKey.MAJOR_KEY, cid);
        ContentVo contentVo = (ContentVo) valueOperations.get(contentKey);
        if (null == contentVo) {
            if (StringUtils.isNotBlank(cid)) {
                if (Tools.isNumber(cid)) {
                    contentVo = contentVoMapper.selectByPrimaryKey(Integer.valueOf(cid));
                    if (contentVo != null) {
                        contentVo.setHits(contentVo.getHits() + 1);
                        contentVoMapper.updateByPrimaryKey(contentVo);
                    }
                    valueOperations.set(contentKey, contentVo);
                    redisService.expireKey(contentKey, ContentKey.LIVE_TIME, TimeUnit.HOURS);
                    return contentVo;
                } else {
                    ContentVoExample contentVoExample = new ContentVoExample();
                    contentVoExample.createCriteria().andSlugEqualTo(cid);
                    List<ContentVo> contentVoList = contentVoMapper.selectByExampleWithBLOBs(contentVoExample);
                    if (contentVoList.size() != 1) {
                        throw new TipException("query content by id and return is not one");
                    }
                    contentVo = contentVoList.get(0);
                    valueOperations.set(contentKey, contentVo);
                    redisService.expireKey(contentKey, ContentKey.LIVE_TIME, TimeUnit.HOURS);
                    return contentVo;
                }
            }
        }
        return contentVo;
    }

    @Override
    public void updateCategory(String oldName, String newName) {
        if (StringUtils.isNotBlank(oldName) && StringUtils.isNotBlank(newName)) {
            ContentVoExample example = new ContentVoExample();
            example.createCriteria().andCategoriesEqualTo(oldName);
            ContentVo contentVo = new ContentVo();
            contentVo.setCategories(newName);
            contentVoMapper.updateByExampleSelective(contentVo, example);
        }
    }

    @Override
    public void updateByCid(ContentVo contentVo) {
        if (null != contentVo && null != contentVo.getCid()) {
            contentVoMapper.updateByPrimaryKeySelective(contentVo);
        }
    }

    @Override
    public void publish(ContentVo contents) {
        checkContent(contents);
        if (StringUtils.isNotBlank(contents.getSlug())) {
            if (contents.getSlug().length() < 5) {
                throw new TipException("路径太短了");
            }
            if (!MyUtils.isSlugPath(contents.getSlug())) {
                throw new TipException("您输入的路径不合法");
            }
            ContentVoExample contentVoExample = new ContentVoExample();
            contentVoExample.createCriteria().andTypeEqualTo(contents.getType()).andSlugEqualTo(contents.getSlug());
            long count = contentVoMapper.countByExample(contentVoExample);
            if (count > 0) {
                throw new TipException("该路径已经存在，请重新输入");
            }
        } else {
            contents.setSlug(null);
        }
        // 去除表情
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        int time = DateKit.getCurrentUnixTime();
        contents.setCreated(time);
        contents.setModified(time);
        contents.setHits(0);
        contents.setCommentsNum(0);

        contentVoMapper.insert(contents);

        String tags = contents.getTags();
        String categories = contents.getCategories();
        Integer cid = contents.getCid();
        System.out.println("cid ===== " + cid);
        metaService.saveMetas(Types.TAG.getType(), tags, cid);
        metaService.saveMetas(Types.CATEGORY.getType(), categories, cid);
    }

    @Override
    public void updateArticle(ContentVo contents) {
        checkContent(contents);
        if (StringUtils.isNotBlank(contents.getSlug())) {
            if (contents.getSlug().length() < 5) {
                throw new TipException("路径太短了");
            }
            if (!MyUtils.isSlugPath(contents.getSlug())) {
                throw new TipException("您输入的路径不合法");
            }
        } else {
            contents.setSlug(null);
        }
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));
        contents.setModified(DateKit.getCurrentUnixTime());
        contentVoMapper.updateByPrimaryKeySelective(contents);

        Integer cid = contents.getCid();
        String key = RedisKeyUtil.getKey(ContentKey.TABLE_NAME, ContentKey.MAJOR_KEY, contents.getSlug());
        redisService.deleteKey(key);

        relationshipService.deleteById(cid, null);
        metaService.saveMetas(Types.TAG.getType(), contents.getTags(), cid);
        metaService.saveMetas(Types.CATEGORY.getType(), contents.getCategories(), cid);
    }

    @Override
    public void deleteArticleById(Integer cid) {
        ContentVo content = this.getContentByCid(cid+"");
        if (null != content) {
            contentVoMapper.deleteByPrimaryKey(cid);
            relationshipService.deleteById(cid, null);
            String cidKey = RedisKeyUtil.getKey(ContentKey.TABLE_NAME, ContentKey.MAJOR_KEY, cid+"");
            String slugKey = RedisKeyUtil.getKey(ContentKey.TABLE_NAME, ContentKey.MAJOR_KEY, content.getSlug());
            if (redisService.existKey(cidKey)) {
                redisService.deleteKey(cidKey);
            }
            if (redisService.existKey(slugKey)) {
                redisService.deleteKey(cidKey);
            }

        }
    }

    public void checkContent(ContentVo contentVo) {
        if (null == contentVo) {
            throw new TipException("文章对象不能为空");
        }
        if (null == contentVo.getTitle()) {
            throw new TipException("文章标题不能为空");
        }
        if (null == contentVo.getContent()) {
            throw new TipException("文章内容不能为空");
        }
        if (contentVo.getTitle().length() > 200) {
            throw new TipException("文章标题太长");
        }
        if (contentVo.getContent().length() > 65000) {
            throw new TipException("文章标题太长");
        }
        if (null == contentVo.getAuthorId()) {
            throw new TipException("请登录后发表文章");
        }
    }

}
