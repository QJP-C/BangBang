package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("他人信息参数")
public class UserInfo extends UserMyInfo{
    //是否关注
    @ApiModelProperty("是否关注")
    private Boolean isFollow;

}
