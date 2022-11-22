package com.qjp.bang.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
/**
 * @author qjp
 */
@Component
public class MD5Utils {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    private static final String salt = "1a2b3c4d";

    //第一次加密
    private static String inputPassToFormPass(String inputPass){
        //md5加密密码前，先对密码进行处理，按以下salt的规则处理密码
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    //第二次加密
    private static String formPassToDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    //实际调用的方法，将第一次加密和第二次加密合并，结果应该一致
    public static String inputPassToDBPass(String inputPass, String salt){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(inputPassToFormPass("123456"));
        //b7797cce01b4b131b433b6acf4add449
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        //b7797cce01b4b131b433b6acf4add449
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));

    }
}