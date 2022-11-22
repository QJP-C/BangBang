package com.qjp.bang.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.qjp.bang.service.SendSms;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author qjp
 */
@Service
public class SendSmsImpl implements SendSms {

    @Override
    public boolean addSendSms(String PhoneNumbers, String TemplateCode, Map code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tEgEn2SFs9NTeMH2QJ1", "5qoYF5ptXC2F8j0WXGoFC27CmHBt8K");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("V1.0");
        request.setAction("SendSms");

        //自定义信息
        request.putQueryParameter("PhoneNumbers", PhoneNumbers); //发送至手机号
        request.putQueryParameter("SignName", "帮帮");  //自己配置的短信签名
        request.putQueryParameter("TemplateCode", TemplateCode); //自己配置的模板 模版CODE

        //构建一个短信验证码

        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(code));   //转换成json字符串
        try {
            CommonResponse response = client.getCommonResponse(request); //发送至客户端
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();//返回是否发送成功
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return false;
    }
}

