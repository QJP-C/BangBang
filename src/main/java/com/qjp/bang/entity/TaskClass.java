package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
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
    private Integer id;
    //父节点id 0为根节点
    private Integer father;
    //名称
    private String name;

}

