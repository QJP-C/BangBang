package com.qjp.xjbx.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * JWT token
 */

public class JWTUtils {

    private static final String SIGN = "XYSisQJPdeEZ";  //签名

    //    生成token
    public static String getToken(Map<String,String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);      //时间
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        //payload
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });

        String token = builder.withExpiresAt(instance.getTime())    //过期时间
                .sign(Algorithm.HMAC256(SIGN));   //签名
        return token;
    }


    //    验证token合法性,如果返回了DecodedJWT，说明验证成功，同时返回DecodedJWT以便于；如果未返回DecodedJWT，一定是验签过程中出错了。
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }
}
