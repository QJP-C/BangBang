package com.qjp.bang.dto;

import com.qjp.bang.pojo.Task;
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
    private String userName;
    private String cause ;
    private Integer isPass ;
    private LocalDateTime submissionTime;
    private LocalDateTime auditTime;


}
