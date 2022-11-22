package com.qjp.bang.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qjp
 */
@Data
public class MsgDto {
    private String id;
    private String head;
    private String name;
    private String lastMsg;
    private int isRead;
    private boolean isLogin;
    private LocalDateTime sendTime;
}
