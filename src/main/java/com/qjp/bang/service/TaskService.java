package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskDetailsResultDto;
import com.qjp.bang.dto.TaskListResDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.entity.Task;

import java.util.List;

/**
 * (Task)表服务接口
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
public interface TaskService extends IService<Task> {

    R<String> newTask(String openid, TaskNewDto taskNewDto);

    R<TaskDetailsResultDto> taskDetails(String openid, String taskId);

    R<List<TaskListResDto>> taskList(String openid, String typeId, String search);

    R<List<TaskListResDto>> myList(String openid, Integer status);

    R<List<TaskListResDto>> history(String openid);

    R<List<TaskListResDto>> myLike(String openid);
}

