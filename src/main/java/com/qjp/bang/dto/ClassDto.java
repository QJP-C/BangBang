package com.qjp.bang.dto;

import com.qjp.bang.pojo.Kind;
import com.qjp.bang.pojo.TaskClass;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * @author qjp
 */
@Data
public class ClassDto extends TaskClass {

    private List<Kind> kindList = new ArrayList<>();
}
