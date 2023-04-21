package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * (TaskClass)表实体类
 *
 * @author makejava
 * @since 2023-04-17 19:48:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("任务分类")
public class TaskClass {
    //id
    private String id;
    //名称
    private String name;
    @ApiModelProperty("打开 图片")
    private String onImg;
    @ApiModelProperty("关闭 图片")
    private String offImg;
}

