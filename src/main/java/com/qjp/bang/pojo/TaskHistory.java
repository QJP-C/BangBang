package com.qjp.bang.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:QJP
 * @Date: 2022/11/23  17:45
 * @Version 1.0
 */
@Data
public class TaskHistory {

    private String  id;
    private String  userId;
    private String  taskId;
    private LocalDateTime time;

}
