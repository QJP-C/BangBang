package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.mapper.TaskMapper;
import com.qjp.xjbx.pojo.Kind;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskClass;
import com.qjp.xjbx.pojo.TaskLike;
import com.qjp.xjbx.service.KindService;
import com.qjp.xjbx.service.TaskClassService;
import com.qjp.xjbx.service.TaskLikeService;
import com.qjp.xjbx.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    private TaskClassService taskClassService;
    @Autowired
    private KindService kindService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TaskLikeService taskLikeService;

    /**
     * 查指定
     * @param id
     * @return
     */
    @Override
    public TaskDto  getOne(String id){
        Task task=this.getById(id);
        TaskDto taskDto = new TaskDto();
        BeanUtils.copyProperties(task,taskDto);
        LambdaQueryWrapper<TaskClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskClass::getId,task.getTypeId());
        TaskClass one = taskClassService.getOne(wrapper);
        String type = one.getType();
        LambdaQueryWrapper<Kind> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Kind::getId,taskDto.getKindId());
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
            wrapper.eq(TaskClass::getId,taskDto.getTypeId());
            TaskClass one = taskClassService.getOne(wrapper);
            taskDto.setClassName(one.getType());
            LambdaQueryWrapper<Kind> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Kind::getId,taskDto.getKindId());
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
    public List<TaskDto> my(String id,String name,String location,String state){

        LambdaQueryWrapper<Task> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Task::getUser1Id,id)
                .like(null!=name,Task::getName,name)
                .like(null!=location,Task::getLocation,location)
                .eq(null!=state,Task::getState,state)
                //以更新时间排序
                .orderByDesc(Task::getUpdateTime);
        List<Task> all = this.list(wrapper1);
        List<TaskDto> taskDtos = all.stream().map((item)->{
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(item,taskDto);
            return taskDto;
        }).collect(Collectors.toList());
        for (TaskDto taskDto : taskDtos) {
            //加分类 类别
            LambdaQueryWrapper<TaskClass> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskClass::getId,taskDto.getTypeId());
            TaskClass one = taskClassService.getOne(wrapper);
            taskDto.setClassName(one.getType());
            LambdaQueryWrapper<Kind> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(Kind::getId,taskDto.getKindId());
            Kind kind = kindService.getOne(wrapper2);
            taskDto.setKindName(kind.getName());
        }
        return taskDtos;
    }

    /**
     * 查分类列表
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

    /**
     * 查分页
     */
    @Override
    public Page<TaskDto> pageR(int page, int pageSize, String name,String location){
        //分页构造器对象
        Page<Task> pageInfo = new Page<>(page, pageSize);
        //dto
        Page<TaskDto> dtoPage = new Page<>();
        //查所有待接单的符合条件的订单，按时间降序排序
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(null != name,Task::getName,name)
               .like(null != location,Task::getLocation,location)
               .eq(Task::getState,6);
        wrapper.orderByDesc(Task::getUpdateTime);
        this.page(pageInfo,wrapper);
        //拷贝分页对象数据，排除列表
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //将查出来的数据逐条做处理
        List<Task> records = pageInfo.getRecords();
        List<TaskDto>list =records.stream().map((item)->{
            //拿到任务id
            TaskDto taskDto = new TaskDto();
            String id = item.getId();
            //查该用户是否收藏了该任务
            LambdaQueryWrapper<TaskLike> wrapperQueryWrapper = new LambdaQueryWrapper<>();
            wrapperQueryWrapper.eq(TaskLike::getTaskId,id);
            TaskLike one2 = taskLikeService.getOne(wrapperQueryWrapper);
            if (one2 != null) {
                //是
                taskDto.setIsLike(1);
            }else {
                //否
                taskDto.setIsLike(0);
            }
            //拷贝对象数据
            BeanUtils.copyProperties(item,taskDto);
            //查该任务分类和类别信息 set到dto
            String typeId = item.getTypeId();
            LambdaQueryWrapper<TaskClass> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(TaskClass::getId,typeId);
            TaskClass one = taskClassService.getOne(wrapper1);
            taskDto.setClassName(one.getType());
            String kindId = item.getKindId();
            LambdaQueryWrapper<Kind> wrapper2=new LambdaQueryWrapper<>();
            wrapper2.eq(Kind::getId,kindId);
            Kind one1 = kindService.getOne(wrapper2);
            taskDto.setKindName(one1.getName());
            return taskDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    /**
     * 清除缓存
     */
    @Override
    public void deleteR() {
        Set<String> keys = redisTemplate.keys("TaskKind"+"*");
        if(!keys.isEmpty()){
            for (int i = 0; i < keys.size(); i++) {
                String m = (String) keys.toArray()[i];
                Boolean delete = redisTemplate.delete(m);
                log.info("TaskKind:[{}]",delete);
            }
        }
        Set<String> keys1 = redisTemplate.keys("AllTask"+"*");
        if(!keys1.isEmpty()){
            for (int i = 0; i < keys1.size(); i++) {
                String m = (String) keys1.toArray()[i];
                Boolean delete = redisTemplate.delete(m);
                log.info("AllTask:[{}]",delete);
            }
        }
        Set<String> keys2 = redisTemplate.keys("TaskPage"+"*");
        if(!keys2.isEmpty()){
            for (int i = 0; i < keys2.size(); i++) {
                String m = (String) keys2.toArray()[i];
                Boolean delete = redisTemplate.delete(m);
                log.info("TaskPage:[{}]",delete);
            }
        }
        Set<String> keys3 = redisTemplate.keys("myTask"+"*");
        if(!keys3.isEmpty()){
            for (int i = 0; i < keys3.size(); i++) {
                String m = (String) keys3.toArray()[i];
                Boolean delete = redisTemplate.delete(m);
                log.info("myTask:[{}]",delete);
            }
        }
    }

    /**
     * 在Redis添加任务逾期缓存
     * @param limitTime
     * @param taskId
     * @return
     */
    @Override
    public boolean expect(LocalDateTime limitTime,String taskId) {
        //计算时间差
        Duration between = Duration.between(LocalDateTime.now(), limitTime);
        int i = (int) between.toMillis();
        log.info("时间差：{}",i);
        if (i>0){
            redisTemplate.opsForValue().set("tt"+taskId,"任务逾期缓存",i,TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }


}
