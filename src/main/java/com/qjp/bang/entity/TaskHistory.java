package com.qjp.bang.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (TaskHistory)表实体类
 *
 * @author makejava
 * @since 2023-04-19 18:06:11
 */
@Data
public class TaskHistory {
    
    private String id;
    //用户id
    private String userId;
    //任务
    private String taskId;
    //浏览时间
    private LocalDateTime browseTime;


    }

