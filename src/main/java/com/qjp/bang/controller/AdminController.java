package com.qjp.bang.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.AuditDto;
import com.qjp.bang.pojo.Task;
import com.qjp.bang.pojo.TaskAudit;
import com.qjp.bang.pojo.User;
import com.qjp.bang.service.TaskAuditService;
import com.qjp.bang.service.TaskService;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qjp
 */
@RestController
@Slf4j
@CrossOrigin
@Transactional
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAuditService taskAuditService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OnlineMsController onlineMsController;
    /**
     * 管理员登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<Map<String,Object>>   login(@RequestBody User user){
        Map<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<User> wrap = new LambdaQueryWrapper<>();
        wrap    .eq(User::getPhone,user.getPhone())
                .eq(User ::getPassword,user.getPassword());
        User one = userService.getOne(wrap);
        if (one != null) {
            if ((one.getPermissions()==1)||(one.getPermissions()==2)) {
                log.info("yes");
                Map<String, String> payload = new HashMap<>();
                payload.put("id", one.getId());
                payload.put("phone", one.getPhone());
                payload.put("permissions", String.valueOf(one.getPermissions()));

                //生成JWT的令牌
                String token = JWTUtils.getToken(payload);
                map.put("state", true);
                map.put("msg", "登录成功");
                map.put("token", token);//响应token
                //缓存token
                redisTemplate.opsForValue().set(one.getId(), token, 7, TimeUnit.DAYS);
                return R.success(map,"登录成功！");
            }
            return R.error("您不是管理员哦！");
        }
        return R.error("账号或密码错误！");
    }
    /**
     * 查所有审核任务
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/all")
    public R<Page<AuditDto>> auditListR(@RequestHeader(value="token") String token,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize,
                                        @RequestParam(required = false) Integer state){

        Page<Task> auditPage = new Page<>(page,pageSize);
        Page<AuditDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.orderByDesc(Task::getReleaseTime)
        .like(state!=null,Task::getState,state);
        taskService.page(auditPage,wrap);

        BeanUtils.copyProperties(auditPage,dtoPage,"records");
        List<Task> records = auditPage.getRecords();
        List<AuditDto> auditDtos =records.stream().map((item)->{
            AuditDto auditDto = new AuditDto();
            BeanUtils.copyProperties(item,auditDto);
            User byId = userService.getById(item.getUser1Id());
            auditDto.setUserName(byId.getUsername());
            String taskId = item.getId();
            LambdaQueryWrapper<TaskAudit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskAudit ::getTaskId ,taskId);
            TaskAudit one = taskAuditService.getOne(wrapper);
            auditDto.setAuditId(one.getId());
            auditDto.setAuditer(one.getAuditer());
            auditDto.setAuditerName(one.getAuditerName());
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
        taskService.deleteR();
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
        Task byId = taskService.getById(audit.getTaskId());
        byId.setState(6);
        LambdaQueryWrapper<Task> wrap = new LambdaQueryWrapper<>();
        wrap.eq(Task ::getId,audit.getTaskId());
        taskService.update(byId,wrap);
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
        taskService.deleteR();
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
