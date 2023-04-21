package com.qjp.bang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * (TaskCollect)表实体类
 *
 * @author makejava
 * @since 2023-04-18 11:19:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskCollect {

    private String id;
    //用户id
    private String userId;
    //任务id
    private String taskId;
    //收藏时间
    private LocalDateTime collectTime;


}

