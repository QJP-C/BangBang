package com.qjp.bang.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskDetailsResultDto;
import com.qjp.bang.dto.TaskListResDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.entity.*;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.TaskMapper;
import com.qjp.bang.service.*;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qjp.bang.common.Constants.REDIS_COUNTDOWN_KEY;

/**
 * (Task)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
@Service("taskService")
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Resource
    private UserService userService;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskClassService taskClassService;
    @Resource
    private FileService fileService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TaskCollectService taskCollectService;
    @Resource
    private TaskHistoryService taskHistoryService;

    /**
     * 发布任务
     * @param openid
     * @param taskNewDto
     * @return
     */
    @Override
    @Transactional
    public R<String> newTask(String openid, TaskNewDto taskNewDto) {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task();
        BeanUtils.copyProperties(taskNewDto, task);
        task.setFromId(openid);
        task.setReleaseTime(now);
        task.setState(0);//待审核
        int insert = taskMapper.insert(task);
        if (insert == 0){
            BangException.cast("发布失败!");
        }
        String taskId = task.getId();
        String[] urls = taskNewDto.getUrls();
        for (String url : urls) {
            File file = new File();
            file.setAboutId(taskId);
            file.setBelong(1);//1发布任务附件
            file.setUrl(url);
            file.setCreateTime(now);
            fileService.save(file);
        }
        if (taskNewDto.getLimitTime() != null) {
            boolean flag = countdown(taskNewDto.getLimitTime(), taskId);
            if (!flag){
                BangException.cast("添加计时任务失败!  截止时间不能在当前时间之前");
            }
        }
        return R.success("发布成功");
    }

    /**
     * 任务详情
     * @param openid
     * @param taskId
     * @return
     */
    @Override
    public R<TaskDetailsResultDto> taskDetails(String openid, String taskId) {
        Task task = this.getById(taskId);
        TaskDetailsResultDto dto = new TaskDetailsResultDto();
        BeanUtils.copyProperties(task,dto);
        //该任务当前用户是否收藏
        int like = isLike(openid, taskId);
        dto.setIsLike(like);
        //用户信息
        Map<String, String> oneInfo = userService.getOneInfo(task.getFromId());
        dto.setFromHead(oneInfo.get("head"));
        dto.setFromName(oneInfo.get("username"));
        //分类信息
        dto.setType(className(task.getType()));
        //获取附件url
        String[] fromFiles = files(taskId, "1");
        if (fromFiles!=null){
            dto.setFromUrls(fromFiles);
        }
        //查看是否有接单人
        if (!StringUtil.isNullOrEmpty(task.getToId())){//有接单人
            //用户信息
            Map<String, String> toInfo = userService.getOneInfo(task.getToId());
            dto.setToHead(toInfo.get("head"));
            dto.setToName(toInfo.get("username"));
            //是否有提交的附件
            String[] toFiles = files(taskId, "2");
            if (ArrayUtil.isEmpty(toFiles)){
                dto.setToUrls(toFiles);
            }
        }

        //保存历史足迹
        boolean b = taskHistoryService.addHistory(openid, taskId);
        if (!b) BangException.cast("保持历史记录失败，请重试!");
        return R.success(dto);
    }

    /**
     * 任务列表
     *
     * @param openid
     * @param typeId
     * @param search
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<TaskListResDto>> taskList(String openid, String typeId, String search, int page, int pageSize) {
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        //是否按类型查询
        if (typeId!=null){
            qw.eq(Task::getType,typeId);
        }
        //是否有搜索字段
        if (search!=null){
            qw.like(Task::getLocation,search)
                    .or()
                    .like(Task::getDetails,search)
                    .or()
                    .like(Task::getTitle,search);
        }
        qw.orderByDesc(Task::getReleaseTime);
        return R.success(getListR(openid, qw,page,pageSize));
    }

    /**
     * 我的发布
     *
     * @param openid
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<TaskListResDto>> myList(String openid, Integer status, int page, int pageSize) {
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        qw.eq(Task::getFromId,openid);
        //是否按状态查询
        if (status!=null){
            qw.eq(Task::getState,status);
        }
        qw.orderByDesc(Task::getReleaseTime);
        return R.success(getListR(openid, qw,page,pageSize));
    }

    /**
     * 我的足迹
     *
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<TaskListResDto>> history(String openid, int page, int pageSize) {
        LambdaQueryWrapper<TaskHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(TaskHistory::getUserId,openid);
        qw.orderByDesc(TaskHistory::getBrowseTime);
        //分页构造器对象
        Page<TaskHistory> pageInfo = new Page<>(page, pageSize);
        Page<TaskListResDto> dtoPage = new Page<>(page, pageSize);

        taskHistoryService.page(pageInfo,qw);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<TaskHistory> records = pageInfo.getRecords();
        List<TaskListResDto> list = records.stream().map(i -> {
            Task task = this.getById(i.getTaskId());
            return getTaskListResDto(openid, task);
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 我的收藏
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<TaskListResDto>> myCollect(String openid, int page, int pageSize) {
        //分页构造器对象
        Page<TaskCollect> pageInfo = new Page<>(page, pageSize);
        Page<TaskListResDto> dtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<TaskCollect> qw = new LambdaQueryWrapper<>();
        qw.eq(TaskCollect::getUserId,openid).orderByDesc(TaskCollect::getCollectTime);
        taskCollectService.page(pageInfo,qw);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<TaskCollect> records = pageInfo.getRecords();
        List<TaskListResDto> list = records.stream().map(i -> {
            Task task = this.getById(i.getTaskId());
            return getTaskListResDto(openid, task);
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 获取列表
     *
     * @param openid
     * @param qw
     * @return
     */
    @NotNull
    private Page<TaskListResDto> getListR(String openid, LambdaQueryWrapper<Task> qw, int page, int pageSize) {
        //分页构造器对象
        Page<Task> pageInfo = new Page<>(page, pageSize);
        Page<TaskListResDto> dtoPage = new Page<>(page, pageSize);
        this.page(pageInfo,qw);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Task> list = pageInfo.getRecords();
        List<TaskListResDto> res = list.stream().map(task -> {
            return getTaskListResDto(openid, task);
        }).collect(Collectors.toList());
        dtoPage.setRecords(res);
        return dtoPage;
    }

    /**
     * 获取任务相关信息
     * @param openid
     * @param task
     * @return
     */
    @NotNull
    private TaskListResDto getTaskListResDto(String openid, Task task) {
        TaskListResDto dto = new TaskListResDto();
        BeanUtils.copyProperties(task, dto);
        //用户信息
        Map<String, String> oneInfo = userService.getOneInfo(task.getFromId());
        dto.setHead(oneInfo.get("head"));
        dto.setUsername(oneInfo.get("username"));
        int like = isLike(openid, task.getId());
        dto.setIsLike(like);
        return dto;
    }

    /**
     * 获取文件数组
     * @param taskId
     * @param beLong
     * @return
     */
    private String[] files(String taskId, String beLong){
        LambdaQueryWrapper<File> qw = new LambdaQueryWrapper<>();
        qw.eq(File::getAboutId,taskId).eq(File::getBelong,beLong);
        int count = fileService.count();
        if (count<=0){
            return null;
        }
        List<File> list = fileService.list(qw);
        String[] res = new String[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i).getUrl();
        }
        return res;
    }

    /**
     * 获取该订单分类名称
     * @param typeId
     * @return
     */
    private String className(String typeId){
        TaskClass son = taskClassService.getById(typeId);
        return son.getName();
    }




    /**
     * 获取该用户是否收藏该任务
     * @param openid
     * @param taskId
     * @return
     */
    private int isLike(String openid, String taskId) {
        LambdaQueryWrapper<TaskCollect> qw = new LambdaQueryWrapper<>();
        qw.eq(TaskCollect::getUserId, openid).eq(TaskCollect::getTaskId, taskId);
        return taskCollectService.count(qw);
    }

    /**
     * 向redis添加任务截止倒计时任务
     *
     * @param limitTime
     * @param taskId
     * @return
     */
    public boolean countdown(LocalDateTime limitTime, String taskId) {
        //计算时间差
        Duration between = Duration.between(LocalDateTime.now(), limitTime);
        long i = (int) between.toMillis();
        if (i <= 0) {
            return false;
        }
        stringRedisTemplate.opsForValue().set(REDIS_COUNTDOWN_KEY + taskId, "任务逾期缓存", i, TimeUnit.MILLISECONDS);
        return true;
    }
}

