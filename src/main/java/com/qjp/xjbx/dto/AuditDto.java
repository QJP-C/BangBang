package com.qjp.xjbx.dto;

import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskAudit;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qjp
 */
@Data
public class AuditDto extends Task {
    private String auditId;
    private String auditer;
    private String auditerName;
    private String cause ;
    private Integer isPass ;
    private LocalDateTime submissionTime;
    private LocalDateTime auditTime;


}
