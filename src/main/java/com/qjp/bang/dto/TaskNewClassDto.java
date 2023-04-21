package com.qjp.bang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TaskNewClassDto {
    @ApiModelProperty("分类名称")
    private String name;
    @ApiModelProperty("打开 图片")
    private String onImg;
    @ApiModelProperty("关闭 图片")
    private String offImg;
}
