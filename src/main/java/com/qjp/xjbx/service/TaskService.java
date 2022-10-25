package com.qjp.xjbx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.pojo.Task;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskService extends IService<Task> {
    void saveWithClass(TaskDto dto);
    TaskDto getOne(String id);
    List<TaskDto> getAlls();
    List<TaskDto> my(String id);
    List<ClassDto> getClassDto();
}
