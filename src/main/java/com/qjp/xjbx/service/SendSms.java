package com.qjp.xjbx.service;


import java.util.Map;

public interface SendSms {
    boolean addSendSms(String PhoneNumbers, String TemplateCode, Map code);
}

