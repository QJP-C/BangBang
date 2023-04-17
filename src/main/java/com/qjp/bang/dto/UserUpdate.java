package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("更新个人资料参数")
public class UserUpdate {
    //邮箱
    @ApiModelProperty("邮箱")
    private String email;
    //昵称
    @ApiModelProperty("昵称")
    private String username;
    //性别  1男 0女
    @ApiModelProperty("性别  1男 0女")
    private Integer sex;
    //手机号
    @ApiModelProperty("手机号")
    private String phone;
    //头像
    @ApiModelProperty("头像url")
    private String head;
    //主页背景
    @ApiModelProperty("主页背景url")
    private String background;
}
