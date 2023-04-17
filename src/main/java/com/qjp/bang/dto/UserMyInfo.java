package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel("个人信息参数")
public class UserMyInfo {
    //用户id
    @ApiModelProperty("用户id")
    private String id;
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
    @ApiModelProperty("头像")
    private String head;
    //主页背景
    @ApiModelProperty("主页背景")
    private String background;
    //签名
    @ApiModelProperty("签名")
    private String signature;
    //获赞数
    @ApiModelProperty("获赞数")
    private Integer nice;
    //关注数
    @ApiModelProperty("关注数")
    private Integer follow;
    //粉丝数
    @ApiModelProperty("粉丝数")
    private Integer fans;
}
