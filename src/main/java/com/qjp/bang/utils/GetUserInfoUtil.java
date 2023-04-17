package com.qjp.bang.utils;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetUserInfoUtil {
    // 请求的网址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    // appid
    public static final String WX_LOGIN_APPID = "wx48f7b5660fa8f60e";    //自己的appid
    // 密匙
    public static final String WX_LOGIN_SECRET = "ef3af8bea39cb01a963160421b81850c";   //自己的secret
    // 固定参数
    public static final String WX_LOGIN_GRANT_TYPE = "authorization_code";

    //通过code换取微信小程序官网获取的信息
    public static JSONObject getResultJson(String code){
        //配置请求参数
        Map<String,String> params = new HashMap<>();
        params.put("appid", WX_LOGIN_APPID);
        params.put("secret",WX_LOGIN_SECRET);
        params.put("js_code",code);
        params.put("grant_type",WX_LOGIN_GRANT_TYPE);

        //向微信服务器发送请求
        String wxRequestResult = HttpClientUtil.doGet(WX_LOGIN_URL,params);

        return JSONObject.fromObject(wxRequestResult);
    }

    //获取openid
    public static String getOpenid(String code){
        return getResultJson(code).getString("openid");
    }

}
