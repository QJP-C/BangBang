package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("新增任务参数")
public class TaskNewDto {
    //任务标题
    @ApiModelProperty(value = "标题",required = true)
    private String title;
    //任务详情
    @ApiModelProperty(value = "任务详情",required = true)
    private String details;
    //是否加急  1:加急  0:不急
    @ApiModelProperty(value = "是否加急  1:加急  0:不急",required = true)
    private Integer urgent;
    //类型id
    @ApiModelProperty(value = "类型id",required = true)
    private String typeId;
    //赏金
    @ApiModelProperty(value = "赏金")
    private Double money;
    //地址
    @ApiModelProperty(value = "地址")
    private String location;
    //截止时间
    @ApiModelProperty(value = "截止时间")
    private LocalDateTime limitTime;
    //附件
    @ApiModelProperty(value = "附件url数组")
    private String[] urls;
}
