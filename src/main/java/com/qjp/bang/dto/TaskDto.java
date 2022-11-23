package com.qjp.bang.dto;

import com.qjp.bang.pojo.Task;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qjp
 */
@Data
public class TaskDto extends Task {
    private String  kindName;

    private String className;

    private Integer isLike;

    private LocalDateTime time;

}
