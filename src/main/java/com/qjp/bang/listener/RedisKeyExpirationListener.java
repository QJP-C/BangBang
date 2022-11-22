package com.qjp.bang.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.bang.pojo.Task;
import com.qjp.bang.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author qjp
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    @Autowired
    private TaskService taskService;
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,message.toString()可以获取失效的key
        String expiredKey = message.toString();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("=========================================================");
        System.out.println("redis key失效 key：" + expiredKey+"现在时间："+now);
        if ("tt".equals(expiredKey.substring(0,2))){
            String tt = expiredKey.replaceFirst("tt", "");
            Task task = new Task();
            task.setState(0);
            LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
            wrap.eq(Task ::getId,tt);
            taskService.update(task,wrap);
            taskService.deleteR();
        }
    }
}