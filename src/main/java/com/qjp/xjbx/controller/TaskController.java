package com.qjp.xjbx.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.service.TaskService;
import com.qjp.xjbx.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 发布任务
     * @param token
     * @param task
     * @return
     */
    @PostMapping("/save")
    public R<String> save(@RequestHeader(value="token") String token,@RequestBody Task task){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        task.setUser1Id(id);
        task.setReleaseTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setState(1);
        taskService.save(task);
        return R.success("保存成功");
    }

    /**
     * 修改
     * @param token
     * @param task
     * @return
     */
    @PutMapping("/update")
    public R<String> update(@RequestHeader(value="token") String token,@RequestBody Task task){
        DecodedJWT verify = JWTUtils.verify(token);
        String email = verify.getClaim("email").asString();
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getId,task.getId());
        task.setUpdateTime(LocalDateTime.now());
        taskService.update(task,wrapper);
        System.out.println();
        return R.success("修改成功");
    }

    /**
     * 查看所有任务
     * @return
     */
    @GetMapping("/all")
    public R<List<Task>>  getRelease(){
        List<Task> list = taskService.list();
        return R.success(list);
    }

    /**
     * 查询自己发布
     * @param token
     * @return
     */
    @GetMapping("/my")
    public R<List<Task>>  getOne(@RequestHeader(value="token") String token){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUser1Id,id);
        List<Task> tasks = taskService.list(wrapper);
        return R.success(tasks,"查询成功");
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
            taskService.update(task, wrapper);
            return R.success("接取成功");
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
