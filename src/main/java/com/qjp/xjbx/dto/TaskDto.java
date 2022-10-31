package com.qjp.xjbx.dto;

import com.qjp.xjbx.pojo.Kind;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskClass;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDto extends Task {
    private String  kindName;

    private String className;

    private Integer isLike;

}
