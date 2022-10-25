package com.qjp.xjbx.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {
    private String email;
    private String id;
    private String password;
    private String username;
    private int state;
    private String phone;
    private int sex;
    private String qq;
    private String head;
    private LocalDateTime birthday;
    private int credibility;
    private int permissions;
}
