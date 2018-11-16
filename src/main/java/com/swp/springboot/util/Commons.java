package com.swp.springboot.util;

import com.github.pagehelper.PageInfo;
import com.swp.springboot.constant.WebConst;
import com.swp.springboot.dto.MetaDto;
import com.swp.springboot.modal.vo.ContentVo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 描述:
 * 统一链接处理
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-30 11:20 AM
 */

@Component
public class Commons {

    public static String gravatar(String email) {
        String avatarUrl = "https://secure.gravatar.com/avatar";
        if (StringUtils.isBlank(email)) {
            return avatarUrl;
        }
        String hash = MyUtils.MD5encode(email.trim().toLowerCase());
        return avatarUrl + "/" + hash;
    }

    /**
     *
     * 网站标题
     * @return
     */
    public static String site_title() {
        return site_option("site_title");
    }

    /**
     * 网站链接
     *
     * @return
     */
    public static String site_url(){
        return site_url("/page/1");
    }

    /**
     * 返回网站链接下的全地址
     *
     * @param sub 后面追加的地址
     * @return
     */
    public static String site_url(String sub) {
        return site_option("site_url", sub);
    }

    /**
     * 网站配置项
     *
     * @param key
     * @return
     */
    public static String site_option(String key) {
        return site_option(key, "");
    }

    /**
     * 网站配置项
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String site_option(String key, String defaultValue) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        String str = WebConst.initConfig.get(key);
        if (StringUtils.isNotBlank(str)) {
            return str;
        } else {
            return defaultValue;
        }
    }

    /**
     * 返回文章链接地址
     *
     * @param contentVo
     * @return
     */
    public static String permalink(ContentVo contentVo) {
        return permalink(contentVo.getCid(), contentVo.getSlug());
    }

    public static String permalink(Integer cid, String slug) {
        return site_url("/article/" + (StringUtils.isNotBlank(slug) ? slug : cid.toString()));
    }

    /**
     * 格式化unix时间戳为日期
     *
     * @param unixTime
     * @return
     */
    public static String fmtdate(Integer unixTime){
        return fmtdate(unixTime, "yyyy-MM-dd");
    }

    /**
     * 格式化unix时间戳为日期
     *
     * @param unixTime
     * @param patten
     * @return
     */
    public static String fmtdate(Integer unixTime, String patten) {
        if (null != unixTime && StringUtils.isNotBlank(patten)) {
            return DateKit.formatDateByUnixTime(unixTime, patten);
        }
        return "";
    }

    /**
     * 判断分页中是否有数据
     *
     * @param paginator
     * @return
     */
    public static boolean is_empty(PageInfo paginator) {
        return paginator == null || (paginator.getList() == null) || (paginator.getList().size() == 0);
    }
    /**
     * 显示分类
     *
     * @param categories
     * @return
     */
    public static String show_categories(String categories) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(categories)) {
            String[] arr = categories.split(",");
            StringBuffer sbuf = new StringBuffer();
            for (String c : arr) {
                sbuf.append("<a class=\"blog-color\" href=\"/category/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
            }
            return sbuf.toString();
        }
        return show_categories("默认分类");
    }

    /**
     * 显示文章缩略图，顺序为：文章第一张图 -> 随机获取
     *
     * @return
     */
    public static String show_thumb(ContentVo contents) {
        if (StringUtils.isNotBlank(contents.getThumbimg())){
            return contents.getThumbimg();
        }
        int cid = contents.getCid();
        int size = cid % 25;
        size = size == 0 ? 1 : size;
        return "/user/img/rand/" + size + ".jpg";
    }

    /**
     * 截取文章摘要
     * @param article
     * @param len
     * @return
     */
    public static String intro(ContentVo article, int len) {
        String value = article.getContent();
        int pos = value.indexOf("<!--more-->");
        if (pos != -1) {
            String substring = value.substring(0, pos);
            return MyUtils.htmlToText(substring);
        } else {
            String text = MyUtils.htmlToText(value);
            if (text.length() > len) {
                return text.substring(0, len);
            }
            return text;
        }
    }

    /**
     * 显示标签
     *
     * @param tags
     * @return
     */
    public static String show_tags(String tags) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(tags)) {
            String[] arr = tags.split(",");
            StringBuffer sbuf = new StringBuffer();
            for (String c : arr) {
                sbuf.append("<a href=\"/tag/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
            }
            return sbuf.toString();
        }
        return "";
    }

    public static String showCategoryUrl(MetaDto metaDto){
        String url = "/category/"+metaDto.getName();
        return url;
    }

    private static final String[] ICONS = {"bg-ico-book", "bg-ico-game", "bg-ico-note", "bg-ico-chat", "bg-ico-code", "bg-ico-image", "bg-ico-web", "bg-ico-link", "bg-ico-design", "bg-ico-lock"};
    /**
     * 显示文章图标
     *
     * @param cid
     * @return
     */
    public static String show_icon(int cid) {
        return ICONS[cid % ICONS.length];
    }

    /**
     * 显示文章内容，转换markdown为html
     *
     * @param value
     * @return
     */
    public static String article(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replace("<!--more-->", "\r\n");
            return MyUtils.mdToHtml(value);
        }
        return "";
    }

    /**
     * An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!
     * <p>
     * 这种格式的字符转换为emoji表情
     *
     * @param value
     * @return
     */
    public static String emoji(String value) {
        return EmojiParser.parseToUnicode(value);
    }

    /**
     * 截取字符串长度
     * @param string
     * @param len
     * @return
     */
    public static String substr(String string, int len) {
        if (string.length() > len) {
            String substring = string.substring(0, len);
            return substring;
        }
        return string;
    }
}
