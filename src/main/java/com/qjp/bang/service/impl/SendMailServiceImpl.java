package com.qjp.bang.service.impl;

import com.qjp.bang.service.SendMailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qjp
 */
@Service
public class SendMailServiceImpl implements SendMailService {
    @Resource
    private JavaMailSender javaMailSender;

    //发送人
    private String from = "915950092@qq.com";
//    //接收人
//    private String to = "915950092@qq.com";
    //标题
    private String subject="来自坤坤的验证码";
    //正文
//    private String context="测试正文内容";

    /**
     * 发送验证码邮件
     * @param email
     * @param code
     */
    @Override
    public void sendMail(String email,String code) {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from+"(爱坤)");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(code);
//        message.setSentDate();定时发送
        javaMailSender.send(message);
    }
}
