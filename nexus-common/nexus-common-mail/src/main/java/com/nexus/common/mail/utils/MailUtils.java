package com.nexus.common.mail.utils;

import com.nexus.common.core.utils.SpringUtils;
import com.nexus.common.core.utils.StringUtils;
import com.nexus.common.mail.config.properties.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.util.Date;


/**
 * 邮件工具
 *
 * @author wk
 * @date 2025/09/14
 */
@Slf4j
public class MailUtils {

    private static final JavaMailSender javaMailSender = SpringUtils.getBean(JavaMailSender.class);

    private static final MailProperties mailProperties = SpringUtils.getBean(MailProperties.class);

    private static final String from;
    private static final String fromName;
    private static final String filePath;

    static {
        from = mailProperties.getFrom();
        fromName = mailProperties.getFromName();
        filePath = mailProperties.getFilePath();
    }

    private MailUtils() {
    }

    /**
     * 发送简单邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text    邮件内容
     */
    public static void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        if (StringUtils.isNotBlank(fromName)) {
            // 如果存在发件人昵称，则设置发件人昵称
            simpleMailMessage.setFrom(fromName + '<' + from + '>');
        } else {
            // 如果没有则直接显示发件人邮箱号
            simpleMailMessage.setFrom(from);
        }
        simpleMailMessage.setTo(to);
        // 设置标题
        simpleMailMessage.setSubject(subject);
        // 设置内容
        simpleMailMessage.setText(text);
        // 邮件发送日期
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

    /**
     * 发送web邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text    邮件内容
     */
    public static void sendWebEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        if (StringUtils.isNotBlank(fromName)) {
            helper.setFrom(fromName + '<' + from + '>');
        } else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSentDate(new Date());
        helper.setSubject(subject);
        // 设置支持 html 解析
        helper.setText(text, true);
        javaMailSender.send(message);
    }

    /**
     * 发送附件邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text    邮件内容
     */
    public static void sendEnclosureEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        // 设置支持附件
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        if (StringUtils.isNotBlank(fromName)) {
            helper.setFrom(fromName + '<' + from + '>');
        } else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        // 添加附件
        File file = new File(filePath);
        helper.addAttachment(file.getName(), file);
        javaMailSender.send(message);
    }

    /**
     * 发送web和附件电子邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text    邮件内容
     */
    public static void sendWebAndEnclosureEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        // 设置支持附件
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        if (StringUtils.isNotBlank(fromName)) {
            helper.setFrom(fromName + '<' + from + '>');
        } else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        // 添加附件
        File file = new File(filePath);
        helper.addAttachment(file.getName(), file);
        javaMailSender.send(message);
    }
}
