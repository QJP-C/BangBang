package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("关注/粉丝列表用户信息")
public class UserListDto {
    //用户id
    @ApiModelProperty("用户id")
    private String id;
    //昵称
    @ApiModelProperty("昵称")
    private String username;
    //头像
    @ApiModelProperty("头像")
    private String head;
    //是否关注
    @ApiModelProperty("是否关注")
    private boolean isFollow;
    //是否互关
    @ApiModelProperty("是否互关")
    private boolean followToo;
}
