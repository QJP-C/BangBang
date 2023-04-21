package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * (TaskAudit)表实体类
 *
 * @author makejava
 * @since 2023-04-18 21:41:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("任务审核模型")
public class TaskAudit {

    private String id;
    //任务id
    private String taskId;
    //任务附件
    private String taskFile;
    //审核时间
    private Date subTime;
    //0：未审核 1：审核通过  2：审核未通过
    private Integer status;
    //审核时间
    private Date auditTime;
    //审核人
    private String auditerId;
    //审核人昵称
    private String auditer;
    //审核理由
    private String reason;


}

