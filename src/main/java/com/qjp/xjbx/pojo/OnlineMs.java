package com.qjp.xjbx.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天
 */
@Data
public class OnlineMs {
    private String   id ;
    private String   fromId ;
    private String   fromName ;
    private String   fromHead;
    private String   lastContext ;
    private String   toId ;
    private String   toHead;
    private LocalDateTime sendTime ;
    private String   toName;
}
