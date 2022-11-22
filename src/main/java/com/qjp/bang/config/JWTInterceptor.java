package com.qjp.bang.config;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.qjp.bang.utils.JWTUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
/**
 * @author qjp
 */
@Component
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 验证token
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


//        log.info("====================JWTInterceptor==========");
        //如果是预检请求，手动加上请求状态200
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return true;
        }

        String token = request.getHeader("token");
        log.info("token:[{}]",token);
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        String permissions = verify.getClaim("permissions").asString();
        log.info("id:[{}]",id);
        log.info("该用户身份:[{}]",permissions);
        String token1 = (String) redisTemplate.opsForValue().get(id);

        Map<String, Object> map = new HashMap<>();
        //获取请求头中令牌
        try {
            JWTUtils.verify(token);//g验证令牌
            if(token.equals(token1)){
                return true;//放行请求
            }
        } catch  (SignatureVerificationException e) {
            e.printStackTrace();
            map.put("msg", "无效签名！");
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            map.put("msg", "token过期！");
        } catch (AlgorithmMismatchException e) {
            e.printStackTrace();
            map.put("msg", "token算法不一致！");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "token:无效！,我劝你别瞎搞");
        }
            map.put("state", false);//设置状态
            map.put("msg", "token已过期,请重新登录");
        //再次登录覆盖 警告重新登陆

            //将map专为json jackson
            String json = new ObjectMapper().writeValueAsString(map);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(json);
        return false;
    }
}
