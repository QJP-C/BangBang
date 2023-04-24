package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskDetailsResultDto;
import com.qjp.bang.dto.TaskListResDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.entity.Task;

/**
 * (Task)表服务接口
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
public interface TaskService extends IService<Task> {
    /**
     * 发布任务
     * @param openid
     * @param taskNewDto
     * @return
     */
    R<String> newTask(String openid, TaskNewDto taskNewDto);

    /**
     * 发布任务
     * @param openid
     * @param taskId
     * @return
     */
    R<TaskDetailsResultDto> taskDetails(String openid, String taskId);

    /**
     * 任务详情
     * @param openid
     * @param typeId
     * @param search
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<TaskListResDto>> taskList(String openid, String typeId, String search, int page, int pageSize);

    /**
     * 我的发布
     * @param openid
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<TaskListResDto>> myList(String openid, Integer status, int page, int pageSize);

    /**
     * 我的足迹
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<TaskListResDto>> history(String openid, int page, int pageSize);

    /**
     * 我的收藏
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<TaskListResDto>> myCollect(String openid, int page, int pageSize);
}

