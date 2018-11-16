package com.swp.springboot.service;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.service
 * @Author: Siwanper
 * @CreateDate: 2018/11/9 下午10:43
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
public interface IMailService {

    /**
     * 发送简单邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendSimpleEmail(String to, String subject, String content);

    /**
     * 发送html邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendHtmlEmail(String to, String subject, String content);

    /**
     * 发送带附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filepath
     */
    public void sendFileMail(String to, String subject, String content, String filepath) ;

}
