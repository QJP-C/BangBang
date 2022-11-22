package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.dto.ClassDto;
import com.qjp.bang.dto.TaskDto;
import com.qjp.bang.pojo.Task;

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
    Page<TaskDto> pageR(int page ,int pageSize ,String condition, String typeId, String kindId,
                        Integer maxMoney, Integer minMoney, Integer urgent,Integer moneySort );
    void deleteR();
    boolean expect(LocalDateTime limitTime,String taskId);
    List<TaskDto> today();
}
