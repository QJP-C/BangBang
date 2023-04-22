package com.qjp.bang.common;

import com.qjp.bang.exception.BangException;
import com.qjp.bang.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    RedisTemplate redisTemplate;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //如果是预检请求，手动加上请求状态200
        log.info("拦截到路径：{}",request.getRequestURI());
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return true;
        }
        //获取请求头token
        String token = request.getHeader("Authorization");
        try{
            jwtUtil.verifyToken(token); //校验token
            String openid = jwtUtil.getOpenidFromToken(token);
            String tokenRedis = Objects.requireNonNull(redisTemplate.opsForValue().get(openid)).toString();
            boolean equals = tokenRedis.equals(token);
            if (equals) return true;
            response.setStatus(508);
            BangException.cast("token过期！");
            return false;//放行请求
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            response.setStatus(508);
            throw new BangException("token过期！");
        }catch (MalformedJwtException e){
            response.setStatus(508);
            e.printStackTrace();
            throw new BangException("token格式错误！");
        }catch (SignatureException e){
            response.setStatus(508);
            e.printStackTrace();
            throw new BangException("无效签名！");
        }catch (IllegalArgumentException e){
            response.setStatus(508);
            e.printStackTrace();
            throw new BangException("非法请求！");
        }catch (Exception e){
            response.setStatus(508);
            e.printStackTrace();
            throw new BangException("token无效！");
        }
    }

}
