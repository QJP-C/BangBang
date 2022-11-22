package com.qjp.bang.service;


import java.util.Map;

/**
 * @author qjp
 */
public interface SendSms {
    boolean addSendSms(String PhoneNumbers, String TemplateCode, Map code);
}

