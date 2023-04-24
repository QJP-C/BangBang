package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.mapper.TaskCollectMapper;
import com.qjp.bang.entity.TaskCollect;
import com.qjp.bang.service.TaskCollectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * (TaskCollect)表服务实现类
 *
 * @author makejava
 * @since 2023-04-18 11:19:58
 */
@Service("taskCollectService")
public class TaskCollectServiceImpl extends ServiceImpl<TaskCollectMapper, TaskCollect> implements TaskCollectService {
    /**
     * 收藏任务
     * @param openid
     * @param taskId
     * @return
     */
    @Override
    public R<String> collectTask(String openid, String taskId) {
        TaskCollect taskCollect = new TaskCollect();
        taskCollect.setUserId(openid);
        taskCollect.setTaskId(taskId);
        LambdaQueryWrapper<TaskCollect> qw = new LambdaQueryWrapper<>(taskCollect);
        int count = this.count(qw);
        if (count > 0) {//取消收藏
            this.remove(qw);
            return R.success("取消收藏");
        }else {
            //收藏
            taskCollect.setCollectTime(LocalDateTime.now());
            this.save(taskCollect);
            return R.success("收藏成功");
        }
    }
}

