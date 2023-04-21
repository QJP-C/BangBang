package com.qjp.bang.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GPTUtil {
    public static String sendPost(String data) {
        RestTemplate client = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer <YourAPI>");
        httpHeaders.add("Content-Type", "application/json"); // 传递请求体时必须设置
//        String requestJson = "{\n" +
//                "    \"model\": \"text-davinci-003\",\n" +
//                "     \"prompt\": \"你好\",\n" +
//                "      \"temperature\": 0, \n" +
//                "      \"max_tokens\": 2048\n" +
//                "}";
        String requestJson = String.format(
                "{\n" +
                        "    \"model\": \"text-davinci-003\",\n" +
                        "     \"prompt\": \"%s\",\n" +
                        "      \"temperature\": 0, \n" +
                        "      \"max_tokens\": 2048\n" +
                        "}",data
        );
        HttpEntity<String> entity = new HttpEntity<String>(requestJson,httpHeaders);
        ResponseEntity<String> response = client.exchange("https://api.openai.com/v1/completions", HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        JSONArray choices = jsonObject.getJSONArray("choices");
        String text = choices.getJSONObject(0).getString("text");
//        Object o = jsonObject.get("\"choices\"");
        return text;
    }

}
