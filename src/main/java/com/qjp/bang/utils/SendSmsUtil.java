package com.qjp.bang.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.qjp.bang.exception.BangException;

import java.util.Map;

import static com.qjp.bang.common.Constants.*;

public class SendSmsUtil {

    public static boolean addSendSms(String PhoneNumbers, Map code) {
        DefaultProfile profile = DefaultProfile.getProfile(SEND_REGIONLD, SEND_ACCESSKEYLD, SEND_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setProduct("Dysmsapi");
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
//        request.setVersion("V1.0");
        request.setAction("SendSms");

        //自定义信息
        request.putQueryParameter("PhoneNumbers", PhoneNumbers); //发送至手机号
        request.putQueryParameter("SignName", "帮帮");  //自己配置的短信签名
        request.putQueryParameter("TemplateCode", SEND_TEMPLATECODE); //自己配置的模板 模版CODE

        //构建一个短信验证码

        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(code));   //转换成json字符串
        try {
            CommonResponse response = client.getCommonResponse(request); //发送至客户端
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();//返回是否发送成功
        } catch (Exception e) {
            BangException.cast(PhoneNumbers+" 短信发送失败错误信息："+e.getMessage());
        }

        return false;
    }
}
