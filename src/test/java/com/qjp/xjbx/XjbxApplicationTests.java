package com.qjp.xjbx;

import com.qjp.xjbx.utils.HttpRestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.IOException;
@Slf4j
@SpringBootTest
class XjbxApplicationTests {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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
}
