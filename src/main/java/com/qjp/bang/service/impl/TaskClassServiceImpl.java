package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskClassDto;
import com.qjp.bang.entity.TaskClass;
import com.qjp.bang.mapper.TaskClassMapper;
import com.qjp.bang.service.TaskClassService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (TaskClass)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 19:48:07
 */
@Service("taskClassService")
public class TaskClassServiceImpl extends ServiceImpl<TaskClassMapper, TaskClass> implements TaskClassService {
    @Override
    public R<Map<Integer, TaskClassDto>> getType() {
        List<TaskClass> list = this.list();
        Map<Integer, TaskClassDto> map = new HashMap<>();
        for (TaskClass aClass : list) {
            //是父节点
            if (aClass.getFather()==0){
                TaskClassDto taskClassDto = new TaskClassDto();
                BeanUtils.copyProperties(aClass,taskClassDto);
                List<TaskClass> list1 = new ArrayList<>();
                taskClassDto.setSon(list1);
                map.put(aClass.getId(),taskClassDto);
            }
        }
        for (TaskClass aClass : list) {
            if (aClass.getFather()!=0){//子节点
                TaskClassDto taskClassDto = map.get(aClass.getFather());
                List<TaskClass> son = taskClassDto.getSon();
                son.add(aClass);
                taskClassDto.setSon(son);
                map.put(aClass.getFather(),taskClassDto);
            }
        }
        return R.success(map);
    }
}

