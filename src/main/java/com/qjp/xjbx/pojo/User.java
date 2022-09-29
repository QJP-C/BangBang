package com.qjp.xjbx.pojo;

import lombok.Data;

/**
 * 用户实体
 */
@Data
public class User {
    private String account;
    private String id;
    private String password;
    private String username;
    private int state;
    private String phone;
    private String sex;
    private String qq;
    private String head;
}
