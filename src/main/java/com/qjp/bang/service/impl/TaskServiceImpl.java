package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.entity.File;
import com.qjp.bang.entity.Task;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.TaskMapper;
import com.qjp.bang.service.FileService;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.qjp.bang.common.Constants.REDIS_COUNTDOWN_KEY;

/**
 * (Task)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
@Service("taskService")
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskClassService taskClassService;
    @Resource
    private FileService fileService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public R<String> newTask(String openid, TaskNewDto taskNewDto) {

        LocalDateTime now = LocalDateTime.now();
        Task task = new Task();
        BeanUtils.copyProperties(taskNewDto, task);
        task.setFromId(openid);
        task.setReleaseTime(now);
        task.setState(0);//待审核
        int insert = taskMapper.insert(task);
        if (insert == 0){
            BangException.cast("发布失败!");
        }
        String taskId = task.getId();
        String[] urls = taskNewDto.getUrls();
        for (String url : urls) {
            File file = new File();
            file.setAboutId(taskId);
            file.setBelong(1);//1发布任务附件
            file.setUrl(url);
            file.setCreateTime(now);
            fileService.save(file);
        }
        if (taskNewDto.getLimitTime() != null) {
            boolean flag = countdown(taskNewDto.getLimitTime(), taskId);
            if (!flag){
                BangException.cast("添加计时任务失败!  截止时间不能在当前时间之前");
            }
        }
        return R.success("发布成功");
    }

    /**
     * 向redis添加任务截止倒计时任务
     *
     * @param limitTime
     * @param taskId
     * @return
     */
    public boolean countdown(LocalDateTime limitTime, String taskId) {
        //计算时间差
        Duration between = Duration.between(LocalDateTime.now(), limitTime);
        long i = (int) between.toMillis();
        if (i <= 0) {
            return false;
        }
        redisTemplate.opsForValue().set(REDIS_COUNTDOWN_KEY + taskId, "任务逾期缓存", i, TimeUnit.MILLISECONDS);
        return true;
    }
}

