package com.qjp.bang.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核
 * @author qjp
 */
@Data
public class TaskAudit {
    private String id ;
    private String taskId;
    private String auditerName;
    private String auditer;
    private String cause ;
    private Integer isPass ;
    private LocalDateTime submissionTime;
    private LocalDateTime auditTime;
}
