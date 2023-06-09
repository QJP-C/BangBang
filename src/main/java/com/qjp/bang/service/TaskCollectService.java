package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.entity.TaskCollect;

/**
 * (TaskCollect)表服务接口
 *
 * @author makejava
 * @since 2023-04-18 11:19:58
 */
public interface TaskCollectService extends IService<TaskCollect> {

    R<String> likeTask(String openid, String taskId);
}

