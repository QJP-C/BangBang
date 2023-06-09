package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskNewClassDto;
import com.qjp.bang.entity.TaskClass;
import com.qjp.bang.mapper.TaskClassMapper;
import com.qjp.bang.service.TaskClassService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (TaskClass)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 19:48:07
 */
@Service("taskClassService")
public class TaskClassServiceImpl extends ServiceImpl<TaskClassMapper, TaskClass> implements TaskClassService {
    @Override
    public R<List<TaskClass>> getType() {
        return R.success(this.list());
    }

    @Override
    public R<String> newClass(TaskNewClassDto taskNewClassDto) {
        TaskClass taskClass = new TaskClass();
        BeanUtils.copyProperties(taskNewClassDto,taskClass);
        boolean save = this.save(taskClass);
        return save ? R.success("新增成功") : R.error("新增失败");
    }
}

