package com.qjp.xjbx.config;

import com.qjp.xjbx.controller.MessageController;
import com.qjp.xjbx.service.OnlineMsService;
import com.qjp.xjbx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 开启WebSocket支持
 * @author qq
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入一个ServerEndpointExporter,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    @Autowired
    private void setRedisTemplate(OnlineMsService onlineMsService){
        MessageController.onlineMsService=onlineMsService;
    }
}

