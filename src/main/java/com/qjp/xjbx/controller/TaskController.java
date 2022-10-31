package com.qjp.xjbx.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.mapper.TaskMapper;
import com.qjp.xjbx.pojo.*;
import com.qjp.xjbx.service.*;
import com.qjp.xjbx.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin
@Transactional
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLikeService taskLikeService;
    @Autowired
    private TaskAuditService taskAuditService;
    @Autowired
    private TaskClassService taskClassService;
    @Autowired
    private KindService kindService;


    /**
     * 发布任务
     * @param token
     * @param taskDto
     * @return
     */
    @PostMapping("/save")
    public R<String> save(@RequestHeader(value="token") String token,@RequestBody TaskDto taskDto){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        taskDto.setUser1Id(id);
        taskDto.setReleaseTime(taskDto.getReleaseTime());
        taskDto.setUpdateTime(taskDto.getReleaseTime());
        taskDto.setState(3);
        boolean save = taskService.save(taskDto);
        if (save) {
            taskService.deleteR();
            return R.success("保存成功");
        }
        return R.error("保存失败");
    }

    /**
     * 提交审核并加入缓存倒计时
     * @param token
     * @param taskDto
     * @return
     */
    @PostMapping("/put")
    public R<String> putTaskAudits(@RequestHeader(value="token") String token,@RequestBody TaskDto taskDto){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        //查该任务信息拿到任务id
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getUser1Id,id)
                .eq(Task::getReleaseTime,taskDto.getReleaseTime());
        Task one = taskService.getOne(queryWrapper);
        String taskId = one.getId();
        //构造一条审核数据
        TaskAudit audit = new TaskAudit();
        audit.setTaskId(taskId);
        audit.setIsPass(0);
        audit.setSubmissionTime(LocalDateTime.now());
        boolean save = taskAuditService.save(audit);
        //将该任务信息加入Redis缓存  缓存过期时将任务改为已逾期
        taskService.expect(taskDto.getLimitTime(), taskId);
        if (save) {
            return R.success("提交审核成功");
        }
            return R.error("提交失败");
    }
    /**
     * 修改
     * @param id
     * @param taskDto
     * @return
     */
    @CachePut(value = "OneTask",key = "#id",unless = "#result == null ")
    @PutMapping("/update")
    public R<String> update(String id,@RequestBody TaskDto taskDto){
        taskDto.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getId,id);
        boolean update = taskService.update(taskDto, wrapper);
        log.info("update:{}",update);
        if (update) {
            taskService.deleteR();
            return R.success("修改成功");
        }
        return R.error("修改失败，请联系管理员！");

    }

    /**
     * 查询分类列表
     * @return
     */
    @Cacheable(value = "TaskKind",unless = "#result == null ")
    @GetMapping
    public R<List<ClassDto>> getKind(){
        List<ClassDto> list = taskService.getClassDto();
        return R.success(list);
    }

    /**
     * 查看所有任务
     * @return
     */
    @Cacheable(value = "AllTask",unless = "#result == null ")
    @GetMapping("/all")
    public R<List<TaskDto>>  getRelease(){
        List<TaskDto> all = taskService.getAlls();
        return R.success(all);
    }
    /**
     * 查看指定
     * @return
     */
    @Cacheable(value = "OneTask",key = "#id",unless = "#result == null ")
    @GetMapping("/one")
    public R<TaskDto>  getRelease1( String id){
        TaskDto one = taskService.getOne(id);
        return R.success(one);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Cacheable(value = "TaskPage",unless = "#result.result.records == null ")
    @GetMapping("/page")
    public R<Page> pageR(int page ,int pageSize ,String name,String location){
        return R.success(taskService.pageR(page, pageSize, name,location));
    }

    /**
     * 查询自己发布
     * @param token
     * @return
     */
    @Cacheable(value = "myTask",unless = "#result == null ")
    @GetMapping("/my")
    public R<List<TaskDto>>  getOne(@RequestHeader(value="token") String token,String name,String location,String state){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        List<TaskDto> one = taskService.my(id,name,location,state);
        return R.success(one);
    }

    /**
     * 接任务
     */
    @PostMapping("/pick")
    public R<String> take(@RequestHeader(value="token") String token, @RequestBody Task task){
        String id = task.getId();
        log.info(id);
        DecodedJWT verify = JWTUtils.verify(token);
        String id2 = verify.getClaim("id").asString();
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getId,id);
        Task one = taskService.getOne(wrapper);
        //看是否是用户自己发布的任务
        if (!Objects.equals(one.getUser1Id(), id2)) {
            task.setUser2Id(id2);
            task.setState(1);
            boolean update = taskService.update(task, wrapper);
            if (update){
                taskService.deleteR();
                return R.success("接取成功");
            }
            return R.error("接取失败");

        }
        return R.error("不能接自己发布的任务哦！");
    }

    /**
     * 删除任务
     * @param id
     * @return
     */
    @CacheEvict(value = "OneTask",key = "#id")
    @DeleteMapping("/delete")
    public R<String> delete(@RequestHeader(value="token") String token,String id){
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("id").asString();
        LambdaQueryWrapper<TaskLike> wrap = new LambdaQueryWrapper<>();
        wrap.eq(TaskLike ::getTaskId,id)
                        .eq(TaskLike::getUserId,userId);
        boolean b = taskService.removeById(id);
        if (b){
            taskLikeService.remove(wrap);
            taskService.deleteR();
            return R.success("删除任务："+id+" 成功！");
        }
        return R.error("删除任务："+id+"失败!");
    }

    /**
     * 收藏任务
     * @param taskId
     * @return
     */
    @GetMapping("/like")
    public R<String> like(@RequestHeader(value="token") String token,@RequestParam String taskId){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        TaskLike taskLike = new TaskLike();
        taskLike.setTaskId(taskId);
        taskLike.setLikeTime(LocalDateTime.now());
        taskLike.setUserId(id);
        boolean save = taskLikeService.save(taskLike);
        if (save) {
            taskService.deleteR();
            return R.success("收藏成功");
        }
        return R.error("收藏失败");
    }
    /**
     * 取消收藏
     */
    @GetMapping("/noLike")
    public R<String> noLike(@RequestHeader(value="token") String token,@RequestParam String id){

        boolean update = taskLikeService.removeById(id);
        if (update){
            taskService.deleteR();
            return R.success("取消收藏成功！");
        }
        return R.error("取消收藏失败！");

    }

    /**
     * 我的收藏
     * @param token
     * @return
     */
    @GetMapping("/ll")
    public R<List<TaskDto>> likeList(@RequestHeader(value="token") String token){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        //查收藏列表
        LambdaQueryWrapper<TaskLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskLike::getUserId,id);
        List<TaskLike> list = taskLikeService.list(wrapper);
        if (list.size() > 0) {
            List<TaskDto> dtoList =list.stream().map((item)->{
                TaskDto taskDto = new TaskDto();
                String taskId = item.getTaskId();
                LambdaQueryWrapper<Task> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(Task::getId,taskId);
                Task one = taskService.getOne(wrapper1);
                BeanUtils.copyProperties(one,taskDto);
                LambdaQueryWrapper<TaskClass> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.eq(TaskClass::getId,one.getTypeId());
                TaskClass oneClass = taskClassService.getOne(wrapper2);
                taskDto.setClassName(oneClass.getType());
                LambdaQueryWrapper<Kind> wrapper3=new LambdaQueryWrapper<>();
                wrapper3.eq(Kind::getId,one.getKindId());
                Kind one1 = kindService.getOne(wrapper3);
                taskDto.setKindName(one1.getName());
                taskDto.setIsLike(1);
                return taskDto;
            }).collect(Collectors.toList());
            return R.success(dtoList);
        }
        return R.error("你还么有收藏嘞，快去任务大厅看看吧~");
    }


    @GetMapping("/sk")
    public R<String> ncsnc(@RequestParam String username,@RequestParam String id){
        log.info("{}",username);
        log.info("{}",id);
        return R.success("success");
    }

}
