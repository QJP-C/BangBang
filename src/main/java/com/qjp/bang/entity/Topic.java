package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * (Topic)表实体类
 *
 * @author makejava
 * @since 2023-04-20 20:18:21
 */
@Data
@ApiModel("话题")
public class Topic {

    private String id;
    //话题名称
    @ApiModelProperty("话题名称")
    private String name;
    //话题标签
    @ApiModelProperty("话题标签")
    private String tags;
    //话题头像
    @ApiModelProperty("话题头像")
    private String head;
    //话题背景
    @ApiModelProperty("话题背景")
    private String bc;
}

