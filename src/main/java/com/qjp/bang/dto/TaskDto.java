package com.qjp.bang.dto;

import com.qjp.bang.pojo.Task;
import lombok.Data;

/**
 * @author qjp
 */
@Data
public class TaskDto extends Task {
    private String  kindName;

    private String className;

    private Integer isLike;

}
