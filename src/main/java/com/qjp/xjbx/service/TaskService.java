package com.qjp.xjbx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.pojo.Task;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qjp
 */
public interface TaskService extends IService<Task> {

    TaskDto getOne(String id);
    List<TaskDto> getAlls();
    List<TaskDto> my(String id,String name,String location,String state);
    List<ClassDto> getClassDto();
    Page<TaskDto> pageR(int page, int pageSize, String name,String location);
    void deleteR();
    boolean expect(LocalDateTime limitTime,String taskId);

}
