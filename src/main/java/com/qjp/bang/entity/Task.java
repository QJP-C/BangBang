package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * (Task)表实体类
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("任务实体类")
public class Task{
    //任务id
    private String id;
    //发布人id
    private String fromId;
    //接单人id
    private String toId;
    //任务标题
    private String title;
    //任务详情
    private String details;
    //是否加急  1:加急  0:不急
    private Integer urgent;
    //任务状态   1:已发布  2:已接取  3:已完成   0:已逾期
    private Integer state;
    //叶类型id
    private Integer sonType;
    //赏金
    private Double money;
    //地址
    private String location;
    //发布时间
    private LocalDateTime releaseTime;
    //截止时间
    private LocalDateTime limitTime;
    //修改时间
    private LocalDateTime updateTime;


}

