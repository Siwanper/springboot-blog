package com.swp.springboot.util;

import com.swp.springboot.constant.WebConst;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述:
 * 工具类
 *
 * @outhor ios
 * @create 2018-10-25 2:22 PM
 */
public class MyUtils {

    private static final Logger logger = LoggerFactory.getLogger(MyUtils.class);

    private static DataSource dataSource;

    private static ReentrantLock lock = new ReentrantLock();

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern SLUG_REGEX = Pattern.compile("^[A-Za-z0-9_-]{5,100}$", Pattern.CASE_INSENSITIVE);
    /**
     * 获取登录的用户
     *
     * @param request
     * @return
     */
    public static UserVo getLoginUser(HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (null == session) {
            return null;
        }
        UserVo user = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
        return user;
    }

    /**
     * MD5加密
     *
     * @param source
     * @return
     */
    public static String MD5encode(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = messageDigest.digest(source.getBytes());
        StringBuffer hexString = new StringBuffer();
        for (byte anEncode : bytes) {
            String hex = Integer.toHexString(0xff & anEncode);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 设置记住密码的cookie
     * 十二小时
     * @param response
     * @param uid
     */
    public static void setCookie(HttpServletResponse response, Integer uid) {
        try {
            String val = Tools.enAes(uid.toString(), WebConst.AES_SALT);
            Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, val);
            cookie.setPath("/");
            cookie.setMaxAge(12 * 60 * 60);
            cookie.setSecure(false);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getCookieUid(HttpServletRequest request) {
        if (null != request) {
            Cookie cookie = cookieRaw(WebConst.USER_IN_COOKIE, request);
            if (cookie != null && cookie.getValue() != null) {
                try {
                    String uid = Tools.deAes(cookie.getValue(), WebConst.AES_SALT);
                    return StringUtils.isNotBlank(uid) && Tools.isNumber(uid) ? Integer.parseInt(uid) : null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Cookie cookieRaw(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public static boolean isSlugPath(String slug){
        if (StringUtils.isNotBlank(slug)) {
            if (slug.contains("/") || slug.contains(" ") || slug.contains(".")) {
                return false;
            }
            Matcher matcher = SLUG_REGEX.matcher(slug);
            return matcher.find();
        }
        return false;
    }

    /**
     * 文件上传根目录
     *
     * @return
     */
    public static String getUploadFilePath() {
        String path = MyUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.length());
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int lastIndex = path.lastIndexOf("/") + 1;
        path = path.substring(0, lastIndex);
        File file = new File("");
        return file.getAbsolutePath() + "/";
    }

    /**
     * 文件的相对路径
     *
     * @param name
     * @return
     */
    public static String getFileKey(String name) {
        String prefix = "/upload/" + DateKit.dateFormat(new Date(), "yyyy/MM");
        if (!new File(getUploadFilePath() + prefix).exists()) {
            new File(getUploadFilePath() + prefix).mkdirs();
        }
        name = StringUtils.trimToNull(name);
        if (null == name) {
            return prefix + "/" + UUID.UU32() + "." + null;
        } else {
            name = name.replace("\\", "/");
            name = name.substring(name.lastIndexOf("/") + 1);
            int index = name.lastIndexOf(".");
            String ext = null;
            if (index >= 0) {
                ext = StringUtils.trimToNull(name.substring(index + 1));
            }
            return prefix + "/" + UUID.UU32() + "." + (ext == null ? null : ext);
        }
    }

    /**
     * 提取html中的文字
     *
     * @param html
     * @return
     */
    public static String htmlToText(String html) {
        if (StringUtils.isNotBlank(html)) {
            return html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        }
        return "";
    }

    /**
     * markdown转换为html
     *
     * @param markdown
     * @return
     */
    public static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser     = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .attributeProviderFactory(context -> new LinkAttributeProvider())
                .extensions(extensions).build();
        String content = renderer.render(document);
        content = Commons.emoji(content);
        return content;
    }

    public static boolean isEmail(String mail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        return matcher.find();
    }

    static class LinkAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
        }
    }

    /**
     * 判断文件是否为图片
     * @param stream
     * @return
     */
    public static boolean isImage(InputStream stream) {
        try {
            Image image = ImageIO.read(stream);
            if (null == image || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

    }

    /**
     * 替换HTML脚本
     *
     * @param value
     * @return
     */
    public static String cleanXSS(String value) {
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }

    /**
     * 退出登录
     * @param session
     * @param response
     */
    public static void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            response.sendRedirect(Commons.site_url());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("注销失败", e);
        }
    }

    /**
     * 获取随机数
     *
     * @param size
     * @return
     */
    public static String getRandomNumber(int size) {
        String num = "";
        for (int i = 0; i < size; i++) {
            double a = Math.random() * 9.0D;
            a = Math.ceil(a);
            int randomNum = (new Double(a)).intValue();
            num = num + randomNum;
        }
        return num;
    }

    public static DataSource getNewDataSource() {
        lock.lock();
        if (dataSource == null) {
            Properties properties = MyUtils.getPropFromFile("classpath:application-jdbc.properties");
            if (properties.size() == 0) {
                return dataSource;
            }
            DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
            managerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            managerDataSource.setPassword(properties.getProperty("spring.datasource.password"));
            String str = "jdbc:mysql://" + properties.getProperty("spring.datasource.url") + "/" + properties.getProperty("spring.datasource.dbname") + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
            managerDataSource.setUrl(str);
            managerDataSource.setUsername(properties.getProperty("spring.datasource.username"));
            dataSource = managerDataSource;
        }
        return dataSource;
    }

    public static Properties getPropFromFile(String filename) {
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(filename)) {
            try {
                FileInputStream inputStream = new FileInputStream(filename);
                properties.load(inputStream);
            }  catch (IOException | TipException e) {
                logger.error("get properties file fail={}" ,e.getMessage());
            }
        }
        return properties;
    }

}
