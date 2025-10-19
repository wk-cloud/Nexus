package com.nexus.common.utils;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;


/**
 * 邮件工具
 *
 * @author wk
 * @date 2025/09/14
 */
@Component
@ConfigurationProperties(prefix = "email")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailUtils {

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 邮件发送人
     * */
    private String from;
    /**
     * 邮件发送人昵称
     * */
    private String fromNickName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 发送简单邮件
     *
     * @param to      来
     * @param subject 主题
     * @param text 邮件内容
     */
    public  void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        if(StringUtils.isNotBlank(fromNickName)){
            // 如果存在发件人昵称，则设置发件人昵称
            simpleMailMessage.setFrom(fromNickName + '<' + from + '>');
        }else {
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
     * @param text 邮件内容
     */
    public void sendWebEmail(String to,String subject,String text) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        if(StringUtils.isNotBlank(fromNickName)){
            helper.setFrom(fromNickName + '<' + from + '>');
        }else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSentDate(new Date());
        helper.setSubject(subject);
        // 设置支持 html 解析
        helper.setText(text,true);
        javaMailSender.send(message);
    }

    /**
     * 发送附件邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text 邮件内容
     */
    public void sendEnclosureEmail(String to,String subject,String text) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        // 设置支持附件
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        if(StringUtils.isNotBlank(fromNickName)){
            helper.setFrom(fromNickName + '<' + from + '>');
        }else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        // 添加附件
        File file = new File(filePath);
        if(StringUtils.isBlank(fileName)){
            fileName = file.getName();
        }
        helper.addAttachment(fileName,file);
        javaMailSender.send(message);
    }

    /**
     * 发送web和附件电子邮件
     *
     * @param to      接收方邮箱
     * @param subject 邮件标题
     * @param text 邮件内容
     */
    public void sendWebAndEnclosureEmail(String to,String subject,String text) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        // 设置支持附件
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        if(StringUtils.isNotBlank(fromNickName)){
            helper.setFrom(fromNickName + '<' + from + '>');
        }else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text,true);
        // 添加附件
        File file = new File(filePath);
        if(StringUtils.isBlank(fileName)){
            fileName = file.getName();
        }
        helper.addAttachment(fileName,file);
        javaMailSender.send(message);
    }
}
