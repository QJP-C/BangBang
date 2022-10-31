package com.qjp.xjbx.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏
 * @author qjp
 */
@Data
public class TaskLike {
    private String id;
    private String userId;
    private String taskId;
    private LocalDateTime likeTime;
}
