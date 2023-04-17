package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskClassDto;
import com.qjp.bang.entity.TaskClass;

import java.util.Map;

/**
 * (TaskClass)表服务接口
 *
 * @author makejava
 * @since 2023-04-17 19:48:07
 */
public interface TaskClassService extends IService<TaskClass> {

    R<Map<Integer, TaskClassDto>> getType();
}

