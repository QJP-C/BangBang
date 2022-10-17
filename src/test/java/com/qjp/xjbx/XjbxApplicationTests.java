package com.qjp.xjbx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.utils.HttpRestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XjbxApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

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
       String t = "{\"time\":\"2022-10-05T21:38:02.945\",\"message\":\"嗷嗷嗷22\"}";
        JSONObject jsonObject = JSONObject.parseObject(t);
        LocalDateTime time = LocalDateTime.parse(jsonObject.getString("time"));
        System.out.println(time);

    }
}
