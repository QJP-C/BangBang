package com.qjp.xjbx.dto;

import com.qjp.xjbx.pojo.Kind;
import com.qjp.xjbx.pojo.TaskClass;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class ClassDto extends TaskClass {

    private List<Kind> kindList = new ArrayList<>();
}
