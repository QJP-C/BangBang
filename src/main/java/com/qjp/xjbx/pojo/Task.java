package com.qjp.xjbx.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务
 * @author qjp
 */
@Data
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String user1Id  ;
    private String user2Id  ;
    private String name  ;
    private String details  ;
    private String material  ;
    private String typeId  ;
    private String kindId  ;
    private int urgent  ;
    private int state  ;
    private Double money  ;
    private String location  ;
    private LocalDateTime releaseTime ;
    private LocalDateTime startTime   ;
    private LocalDateTime limitTime   ;
    private LocalDateTime updateTime;

}
