package com.qjp.xjbx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.pojo.AlipayBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.util.Random;
import java.util.UUID;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/alipay")
public class AlipayController {

    //获取配置文件中的配置信息
    //应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    @Value("${zfb.appId}")
    private String appId;

    //商户私钥 您的PKCS8格式RSA2私钥
    @Value("${zfb.privateKey}")
    private String privateKey;

    //支付宝公钥
    @Value("${zfb.publicKey}")
    private String publicKey;

    //服务器异步通知页面路径
    @Value("${zfb.notifyUrl}")
    private String notifyUrl;

    //页面跳转同步通知页面路径
    @Value("${zfb.returnUrl}")
    private String returnUrl;

    //签名方式
    @Value("${zfb.signType}")
    private String signType;

    //字符编码格式
    @Value("${zfb.charset}")
    private String charset;

    //支付宝网关
    @Value("${zfb.gatewayUrl}")
    private String gatewayUrl;

    private final String format = "json";

    //PC网页段支付，返回的是支付宝账号的登录页面
    @RequestMapping("/pay")
    @ResponseBody
    public String pay(AlipayBean alipayBean) throws AlipayApiException {
        //模拟数据
        alipayBean.setOut_trade_no(UUID.randomUUID().toString().replaceAll("-",""));
        alipayBean.setSubject("订单名称");
        alipayBean.setTotal_amount(String.valueOf(new Random().nextInt(100)));
        alipayBean.setBody("商品描述");

        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, publicKey, signType);
        //PC网页支付使用AlipayTradePagePayRequest传参，下面调用的是pageExecute方法
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        log.info("封装请求支付宝付款参数为:{}", JSON.toJSONString(alipayRequest));

        // 调用SDK生成表单
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        log.info("请求支付宝付款返回参数为:{}", result);
        return result;
    }

    /**
     * 手机扫码支付
     * @param alipayBean
     * @return
     * @throws Exception
     */
    @RequestMapping("/pay2")
    @ResponseBody
    public R<JSONObject> pay2(AlipayBean alipayBean) throws Exception {
        //接口模拟数据
        alipayBean.setOut_trade_no("20210817010101003");
        alipayBean.setSubject("订单名称");
        alipayBean.setTotal_amount(String.valueOf(new Random().nextInt(100)));
        alipayBean.setBody("商品描述");

        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, publicKey, signType);
        //扫码支付使用AlipayTradePrecreateRequest传参，下面调用的是execute方法
        AlipayTradePrecreateRequest precreateRequest = new AlipayTradePrecreateRequest();
        precreateRequest.setReturnUrl(returnUrl);
        precreateRequest.setNotifyUrl(notifyUrl);
        precreateRequest.setBizContent(JSON.toJSONString(alipayBean));
        log.info("封装请求支付宝付款参数为:{}", JSON.toJSONString(precreateRequest));

        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(precreateRequest);
        } catch (AlipayApiException e) {
            throw new Exception(String.format("下单失败 错误代码:[%s], 错误信息:[%s]", e.getErrCode(), e.getErrMsg()));
        }
        log.info("AlipayTradePrecreateResponse = {}", response.getBody());

        /*
        {
        "code": "10000",
        "msg": "Success",
        "out_trade_no": "815259610498863104",
        "qr_code": "https://qr.alipay.com/bax09455sq1umiufbxf4503e"
        }
        */
        if (!response.isSuccess()) {
            throw new Exception(String.format("下单失败 错误代码:[%s], 错误信息:[%s]", response.getCode(), response.getMsg()));
        }
        // TODO 下单记录保存入库
        // 返回结果，主要是返回 qr_code，前端根据 qr_code 进行重定向或者生成二维码引导用户支付
        JSONObject jsonObject = new JSONObject();
        //支付宝响应的订单号
        String outTradeNo = response.getOutTradeNo();
        jsonObject.put("outTradeNo",outTradeNo);
        //二维码地址，页面使用二维码工具显示出来就可以了
        jsonObject.put("qrCode",response.getQrCode());
        return R.success(jsonObject);
    }

    @RequestMapping("/success")
    @ResponseBody
    public String success(){
        return "交易成功！";
    }

    @RequestMapping(value = "/index")
    public String payCoin(){
        return "index.html";
    }
}
