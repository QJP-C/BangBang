package com.qjp.xjbx.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.dto.AuditDto;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskAudit;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.TaskAuditService;
import com.qjp.xjbx.service.TaskService;
import com.qjp.xjbx.service.UserService;
import com.qjp.xjbx.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qjp
 */
@RestController
@Slf4j
@CrossOrigin
@Transactional
@RequestMapping("/audit")
public class TaskAuditController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAuditService taskAuditService;
    @Autowired
    private UserService userService;

    /**
     * 查所有审核任务
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/all")
    public R<Page> auditListR(@RequestHeader(value="token") String token,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize){
        Page<Task> auditPage = new Page<>(page,pageSize);
        Page<AuditDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.orderByDesc(Task::getReleaseTime);
        taskService.page(auditPage,wrap);
        BeanUtils.copyProperties(auditPage,dtoPage,"records");
        List<Task> records = auditPage.getRecords();
        List<AuditDto> auditDtos =records.stream().map((item)->{
            String taskId = item.getId();
            LambdaQueryWrapper<TaskAudit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskAudit ::getTaskId ,taskId);
            TaskAudit one = taskAuditService.getOne(wrapper);
            AuditDto auditDto = new AuditDto();
            auditDto.setAuditId(one.getId());
            BeanUtils.copyProperties(item,auditDto);
            return auditDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(auditDtos);
        return R.success(dtoPage);
    }

    /**
     * 通过审核
     * @param token
     * @return
     */
    @PutMapping("/pass")
    public R<String> pass(@RequestHeader(value="token") String token,
                          @RequestBody TaskAudit audit){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User ::getId,id);
        User one = userService.getOne(wrapper);
        String username = one.getUsername();
        audit.setIsPass(1);
        audit.setAuditTime(LocalDateTime.now());
        audit.setAuditer(id);
        audit.setAuditerName(username);
        LambdaUpdateWrapper<TaskAudit> wrapper1 = new LambdaUpdateWrapper<>();
        wrapper1.eq(TaskAudit::getTaskId,audit.getTaskId());
        taskAuditService.update(audit,wrapper1);
        Task task = new Task();
        task.setState(6);
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.eq(Task ::getId,audit.getTaskId());
        taskService.update(task,wrap);
        return R.success("任务："+audit.getTaskId()+"已审核通过");
    }

    /**
     * 不通过审核
     * @param token
     * @return
     */
    @PutMapping("/nopass")
    public R<String> noPass(@RequestHeader(value="token") String token,
                            @RequestBody TaskAudit audit){
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User ::getId,id);
        User one = userService.getOne(wrapper);
        String username = one.getUsername();
        TaskAudit taskAudit = new TaskAudit();
        taskAudit.setIsPass(2);
        taskAudit.setAuditTime(LocalDateTime.now());
        taskAudit.setAuditer(id);
        taskAudit.setCause(audit.getCause());
        taskAudit.setAuditerName(username);
        LambdaQueryWrapper<TaskAudit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskAudit::getTaskId,audit.getTaskId());
        taskAuditService.update(taskAudit,queryWrapper);
        Task task = new Task();
        task.setState(5);
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.eq(Task ::getId,audit.getTaskId());
        taskService.update(task,wrap);
        return R.success("任务："+audit.getTaskId()+"审核状态变为未通过");
    }
}
