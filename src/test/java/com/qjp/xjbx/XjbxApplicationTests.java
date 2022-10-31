package com.qjp.xjbx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.TaskService;
import com.qjp.xjbx.utils.HttpRestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XjbxApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() {
        String str = "915950092@qq.com" ;

        String[] result = str.split("@") ;
        String sss= result[0];
        System.out.println(sss);
//        for (int i = 0; i < result.length; i++) {
////            String[] temp = result[i].split("=") ;
////            System.out.println(temp[0]+" = "+temp[1]);
//        }
    }
    @Test
    public void register() throws IOException {
        {
            try {
                String qq = "1480069996";
                //api url地址
                String url = "https://api.lixingyong.com/api/qq?id="+qq;
                // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
                MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
                params.add("id",qq);
                System.out.println("发送数据：" + params);
                //发送http请求并返回结果
                String result = HttpRestUtils.get(url,params);
                System.out.println(result);
                String[] attribute =result.split("\\,");
                String un= attribute[1];
                String replace = un.replace("\"", "");
                System.out.println(replace);
                String username= replace.substring(9);
                System.out.println(username);
                String hd =attribute[2];
                String replace1 = hd.replace("\"", "");
                String head = replace1.substring(7);
                System.out.println(head);
                System.out.print("接收反馈：" + result);
//                return result;
            } catch (Exception e) {
                log.info("获取qq头像出现错误");
                log.info(e.getMessage());
                System.out.println("------------- " + this.getClass().toString() + ".PostData() : 出现异常 Exception -------------");
                System.out.println(e.getMessage());
//                return "获取qq头像出现错误";
            }
        }
    }
    @Test
    void sads(){
        String s="{\"qq\":\"949516815\",\"nickname\":\".\",\"avatar\":\"https://q1.qlogo.cn/g?b=qq&nk=949516815&s=40\",\"email\":\"949516815@qq.com\",\"url\":\"https://user.qzone.qq.com/949516815\"}";
        JSONObject jsonObject= JSON.parseObject(s);
        System.out.println(jsonObject.getString("qq"));
    }
    @Test
    void pmpm(){
        try {
            String qq = "1480069996";
            //api url地址
            String url = "https://api.lixingyong.com/api/qq?id="+qq;
//            String url = "https://api.lixingyong.com/api/qq";
            //post请求
//                HttpMethod method = HttpMethod.GET;
            // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.set("id",qq);
            System.out.print("发送数据：" + params);
            //发送http请求并返回结果
            String result = HttpRestUtils.get(url, params);

            System.out.println(result);
            JSONObject jsonObject = JSON.parseObject(result);
            String username= jsonObject.getString("nickname");
            log.info("Username:[{}]",username);
            String head = "http://q1.qlogo.cn/g?b=qq&nk="+qq+"&s=640";
            log.info("head:[{}]",head);
            System.out.print("接收反馈：" + result);
            User user = new User();
            user.setUsername(username);
            user.setHead(head);

        } catch (Exception e) {
            log.info("获取qq头像出现错误");
            log.info(e.getMessage());

        }

    }
    @Test
    void sfon(){
            //get
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String uri = "https://apis.map.qq.com/ws/location/v1/ip?key=VSXBZ-S76LQ-N6K5O-G3BNK-WCTYF-WSF2T";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        String body = response.getBody();
        System.out.println("body:"+body);
        JSONObject jsonObject = JSON.parseObject(body);
        String result = jsonObject.getString("result");
        JSONObject jsonObject1 = JSON.parseObject(result);
        String ad_info = jsonObject1.getString("ad_info");
        System.out.println("地址："+ad_info);
        System.out.println(response);
        System.out.println(response.getBody());

        System.out.println("result:"+result);
        //post
//  MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("user", "你好");
//
//        // 以表单的方式提交
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        //将请求头部和参数合成一个请求
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
//
//        return response.getBody();

//        JSONObject jsonObject = JSON.parseObject(result);
//        String ad_info= jsonObject.getString("ad_info");
//        JSONObject jsonObject2 = JSON.parseObject(ad_info);
//        String address=jsonObject2.getString("nation")+jsonObject.getString("province")
//                +jsonObject.getString("city")+jsonObject.getString("district");
//        System.out.println(address);
    }
    @Test
    void sdlkfnion(){
//        Set keys1 = redisTemplate.boundHashOps("HashKey").keys();
//        System.out.println(keys1);
//        BoundHashOperations hashKey = redisTemplate.boundHashOps("26");
//        Set keys2 = hashKey.keys();
//        System.out.println(keys2);
//        Object o = redisTemplate.opsForHash().get("26", "500");
//        System.out.println(o);
//        RedisOperations operations = redisTemplate.opsForHash().getOperations();
//        System.out.println(operations);

        Set<String> keys = redisTemplate.keys("M"+"*");  //获取M开头的key
        if(!keys.isEmpty()){
            for (int i = 0; i < keys.size(); i++) {
                System.out.println(keys.toArray()[i]);//变为数组取第一个
                String o = (String) keys.toArray()[i];
                String m = o.replaceFirst("M", "");
                System.out.println(m);
            }
        }
        System.out.println( keys);
//        Iterator<String> it1 = keys.iterator();
//        System.out.println("toString: " + it1.toString());
//        while (it1.hasNext()) {
//            resultMap =	redisTemplate.opsForHash().entries(it1.next());
//            System.out.println("isEmpty :  " + resultMap.isEmpty());
//            for (Map.Entry<Object, Object> entry : resultMap.entrySet()) {
//                //这里可以写自己的操作
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue() + ";");
//            }

    }
    @Test
    void skmndo(){
        //连接阿里云
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tEgEn2SFs9NTeMH2QJ1", "5qoYF5ptXC2F8j0WXGoFC27CmHBt8K");
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/
        IAcsClient client = new DefaultAcsClient(profile);

        //构建请求
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers("");
        request.setSignName("");
        request.setTemplateCode("");

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }



    }
    @Test
    void  ssdsd(){
        HashMap<String, String> map = new HashMap<>();
        map.put("phone","18119451226");
        map.put("code","5837");
        int ss=0;
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (key.equals("phone")){
                ss=1;
            }
            System.out.println("key: "+key);
            System.out.println(key + "  " + value);
        }
        System.out.println("ss: "+ss);
    }
    @Test
    void  sdsafcv(){
        Set<String> keys = redisTemplate.keys("TaskKind"+"*");  //获取M开头的所有key
        if(!keys.isEmpty()){
            for (int i = 0; i < keys.size(); i++) {
                String m = (String) keys.toArray()[i];
                Boolean delete = redisTemplate.delete(m);
                log.info("TaskKind:[{}]",delete);
            }
        }
    }
    @Test
    void sxbxc(){
        ArrayList<String> cc = new ArrayList<>();
        cc.add("aa");
        cc.add("bb");
        cc.add("cc");
        System.out.println(cc);
    }
    @Test
    void nmi(){
        LocalDateTime now = LocalDateTime.now();
        log.info("当前时间：{}",now);
//        int i = limitTime.getSecond() - now.getSecond();
        LocalDateTime limitTime = LocalDateTime.now().plusHours(3);
        log.info("之后:{}",limitTime);
        Duration between = Duration.between(now, limitTime);
        long i = between.toMinutes();
        log.info("时间差：{}",i);
    }
    @Test
    void dnis(){
        String ss = "2022-10-28 01:15:27";
        LocalDateTime ss1 = LocalDateTime.parse("2022-10-28T01:15:27");
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.eq(Task::getReleaseTime,ss1);
        Task one = taskService.getOne(wrap);
        System.out.println(one);
    }
    @Test
    void vxfd(){
        int ss=100;
        for (int i = 0; i < 11; i++) {
            ss= (int) (ss*1.5);
            System.out.println(ss);
        }
    }

}
