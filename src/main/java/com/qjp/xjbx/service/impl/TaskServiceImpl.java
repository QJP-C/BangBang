package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.mapper.TaskMapper;
import com.qjp.xjbx.pojo.Kind;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskClass;
import com.qjp.xjbx.service.KindService;
import com.qjp.xjbx.service.TaskClassService;
import com.qjp.xjbx.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    private TaskClassService taskClassService;
    @Autowired
    private KindService kindService;

    /**
     * 新增
     * @param dto
     */
    @Override
    @Transactional
    public void saveWithClass(TaskDto dto){
        this.save(dto);
    };

    /**
     * 查指定
     * @param id
     * @return
     */
    @Override
    @Transactional
    public TaskDto  getOne(String id){
        Task task=this.getById(id);
        TaskDto taskDto = new TaskDto();
        BeanUtils.copyProperties(task,taskDto);
        LambdaQueryWrapper<TaskClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskClass::getId,task.getType());
        TaskClass one = taskClassService.getOne(wrapper);
        String type = one.getType();
        LambdaQueryWrapper<Kind> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Kind::getId,taskDto.getKind());
        Kind kind = kindService.getOne(wrapper1);
        taskDto.setKindName(kind.getName());
        taskDto.setClassName(type);
        return taskDto;
    }

    /**
     * 查所有
     * @return
     */
    @Override
    @Transactional
    public List<TaskDto> getAlls() {
        List<Task> all = this.list();
        log.info("all:{}", all);
        List<TaskDto> taskDtos = all.stream().map((item)->{
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(item,taskDto);
            return taskDto;
        }).collect(Collectors.toList());
        for (TaskDto taskDto : taskDtos) {
            LambdaQueryWrapper<TaskClass> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskClass::getId,taskDto.getType());
            TaskClass one = taskClassService.getOne(wrapper);
            taskDto.setClassName(one.getType());
            LambdaQueryWrapper<Kind> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Kind::getId,taskDto.getKind());
            Kind kind = kindService.getOne(wrapper2);
            taskDto.setKindName(kind.getName());
        }
        return taskDtos;
    }

    /**
     * 查自己发布
     * @param id
     * @return
     */
    @Override
    @Transactional
    public List<TaskDto> my(String id){
        LambdaQueryWrapper<Task> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Task::getUser1Id,id);
        List<Task> all = this.list(wrapper1);
        List<TaskDto> taskDtos = all.stream().map((item)->{
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(item,taskDto);
            return taskDto;
        }).collect(Collectors.toList());
        for (TaskDto taskDto : taskDtos) {
            LambdaQueryWrapper<TaskClass> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskClass::getId,taskDto.getType());
            TaskClass one = taskClassService.getOne(wrapper);
            taskDto.setClassName(one.getType());
            LambdaQueryWrapper<Kind> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Kind::getId,taskDto.getKind());
            Kind kind = kindService.getOne(wrapper2);
            taskDto.setKindName(kind.getName());
        }
        return taskDtos;
    }

    /**
     * 查分列列表
     * @return
     */
    @Override
    public List<ClassDto> getClassDto() {
        List<TaskClass> list = taskClassService.list();
        List<ClassDto> dtos =list.stream().map((item)->{
            ClassDto classDto = new ClassDto();
            BeanUtils.copyProperties(item,classDto);
            LambdaQueryWrapper<Kind> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Kind::getClassId,item.getId());
            List<Kind> list1 =kindService.list(wrapper);
            classDto.setKindList(list1);
            return classDto;
        }).collect(Collectors.toList());
        return dtos;
    }

}
