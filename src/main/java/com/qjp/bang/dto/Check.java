package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("校验参数")
public class Check {
    @ApiModelProperty("手机号码")
    public String phone;
    @ApiModelProperty("验证码")
    public String code;
}
