package com.qjp.bang.dto;

import com.qjp.bang.entity.TaskClass;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class TaskClassDto extends TaskClass {
    private List<TaskClass> son ;
}
