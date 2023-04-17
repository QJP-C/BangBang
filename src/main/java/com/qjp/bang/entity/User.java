package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2023-04-13 16:44:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("用户实体类")
public class User {
    //用户id
    private String id;
    //邮箱
    private String email;
    //密码
    private String password;
    //昵称
    private String username;
    //性别  1男 0女
    private Integer sex;
    //手机号
    private String phone;
    //头像
    private String head;
    //主页背景
    private String background;
    //签名
    private String signature;



}

