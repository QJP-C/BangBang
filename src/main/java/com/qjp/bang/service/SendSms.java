package com.qjp.bang.service;


/**
 * @author qjp
 */
public interface SendSms {
    /**
     * 生成存储并发送验证码
     * @param phone
     * @return
     */
    boolean addSendSms(String phone);
}

