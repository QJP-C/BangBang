package com.qjp.bang.common;

import com.qjp.bang.entity.Task;
import com.qjp.bang.service.TaskService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.qjp.bang.common.Constants.REDIS_COUNTDOWN_KEY;

/**
 * @author qjp
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    @Resource
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
        System.out.println("Redis key失效 key：" + expiredKey+"现在时间："+now);
        if (expiredKey.startsWith(REDIS_COUNTDOWN_KEY)){
            String taskId = expiredKey.replaceFirst(REDIS_COUNTDOWN_KEY, "");
            Task task = new Task();
            task.setState(4);//已逾期
            task.setId(taskId);
            taskService.updateById(task);
        }
    }
}