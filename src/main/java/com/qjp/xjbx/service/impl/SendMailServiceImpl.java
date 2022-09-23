package com.qjp.xjbx.service.impl;

import com.qjp.xjbx.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    @Override
    public void sendMail(String account,String code) {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from+"(爱坤)");
        message.setTo(account);
        message.setSubject(subject);
        message.setText(code);
//        message.setSentDate();定时发送
        javaMailSender.send(message);
    }
}
