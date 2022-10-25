package com.qjp.xjbx.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.dto.ClassDto;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskClass;
import com.qjp.xjbx.service.TaskClassService;
import com.qjp.xjbx.service.TaskService;
import com.qjp.xjbx.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@CrossOrigin
@Transactional
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskClassService taskClassService;
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
        taskDto.setReleaseTime(LocalDateTime.now());
        taskDto.setUpdateTime(LocalDateTime.now());
        taskDto.setState(1);
        boolean save = taskService.save(taskDto);
        if (save) {
            return R.success("保存成功");
        }
        return R.error("保存失败");
    }

    /**
     * 修改
     * @param
     * @param task
     * @return
     */
    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable String id,@RequestBody Task task){
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getId,id);
        task.setUpdateTime(LocalDateTime.now());
        boolean update = taskService.update(task, wrapper);
        log.info("update:{}",update);
        if (update) {
            return R.success("修改成功");
        }
        return R.error("修改失败，请联系管理员！");
    }

    /**
     * 查询分类列表
     * @return
     */
    @GetMapping
    public R<List<ClassDto>> getKind(){
        List<ClassDto> list = taskService.getClassDto();
        return R.success(list);
    }

    /**
     * 查看所有任务
     * @return
     */
    @GetMapping("/all")
    public R<List<TaskDto>>  getRelease(){
        List<TaskDto> all = taskService.getAlls();
        return R.success(all);
    }
    /**
     * 查看指定
     * @return
     */
    @GetMapping("/one/{id}")
    public R<TaskDto>  getRelease1(@PathVariable String id){
        TaskDto one = taskService.getOne(id);
        return R.success(one);
    }


    public R<Page> pageR(int page ,int pageSize ,String name){
            return null;
    }

    /**
     * 查询自己发布
     * @param token
     * @return
     */
    @GetMapping("/my")
    public R<List<TaskDto>>  getOne(@RequestHeader(value="token") String token){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        List<TaskDto> one = taskService.my(id);
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
        if (!Objects.equals(one.getUser1Id(), id2)) {
//            Task task = new Task();
            task.setUser2Id(id2);
            boolean update = taskService.update(task, wrapper);
            if (update){
                return R.success("接取成功");
            }
            return R.error("接取失败");

        }
            return R.error("不能接自己发布的任务哦！");
    }

    /**
     * 删除任务
     * @param token
     * @param task
     * @return
     */
    @DeleteMapping("/delete")
    public R<String> delete(@RequestHeader(value="token") String token,@RequestBody Task task){
        String id = task.getId();
        boolean b = taskService.removeById(id);
        if (b){
            return R.success("删除任务："+id+" 成功！");
        }
        return R.error("删除任务："+id+"失败!");
    }

}
