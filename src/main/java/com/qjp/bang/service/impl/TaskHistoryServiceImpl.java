package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.entity.TaskHistory;
import com.qjp.bang.mapper.TaskHistoryMapper;
import com.qjp.bang.service.TaskHistoryService;
import com.qjp.bang.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * (TaskHistory)表服务实现类
 *
 * @author makejava
 * @since 2023-04-19 18:06:11
 */
@Service("taskHistoryService")
public class TaskHistoryServiceImpl extends ServiceImpl<TaskHistoryMapper, TaskHistory> implements TaskHistoryService {
    @Resource
    UserService userService;


    @Override
    public void addHistory(String openid, String taskId) {
        LambdaQueryWrapper<TaskHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(TaskHistory::getUserId,openid).eq(TaskHistory::getTaskId,taskId);
        int count = this.count(qw);
        if (count>0){
            LambdaQueryWrapper<TaskHistory> qww = new LambdaQueryWrapper<>();
            qww.eq(TaskHistory::getUserId,openid).eq(TaskHistory::getTaskId,taskId);
            this.remove(qww);
        }
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTaskId(taskId);
        taskHistory.setUserId(openid);
        taskHistory.setBrowseTime(LocalDateTime.now());
        this.save(taskHistory);
    }
}

