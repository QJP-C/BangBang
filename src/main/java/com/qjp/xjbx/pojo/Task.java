package com.qjp.xjbx.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;          //任务id
    private String user1Id  ;   //发布人id
    private String user2Id  ;   //接单人id
    private String name  ;      //任务标题
    private String details  ;   //任务详情
    private String material  ;  //任务资料（图片或视频）
    private String type  ;      //任务类型
    private String kind  ;      //任务种类
    private int urgent  ;    //是否加急  1:加急  2:不急
    private int state  ;     //任务状态   1:已发布  2:待接取  3:已接取  4:已完成   0:已逾期
    private Double money  ;     //赏金
    private String location  ;  //地址
    private LocalDateTime releaseTime ;//发布时间
    private LocalDateTime startTime   ;//开始时间
    private LocalDateTime limitTime   ;//限制完成时间
    private LocalDateTime updateTime;  //修改时间

}
