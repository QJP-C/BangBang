package com.qjp.bang.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 * @author qjp
 */
@Data
public class User {
    private String email;
    private String id;
    private String password;
    private String username;
    private Integer state;
    private String phone;
    private Integer sex;
    private String qq;
    private String head;
    private LocalDateTime birthday;
    private Integer credibility;
    private Integer experience;
    private Integer permissions;
    private String background;
}
