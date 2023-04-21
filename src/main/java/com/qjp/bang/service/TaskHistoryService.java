package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.entity.TaskHistory;

/**
 * (TaskHistory)表服务接口
 *
 * @author makejava
 * @since 2023-04-19 18:06:11
 */
public interface TaskHistoryService extends IService<TaskHistory> {

    void addHistory(String openid, String taskId);
}

