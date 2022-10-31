package com.qjp.xjbx.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类
 * @author qjp
 */
@Data
public class TaskClass implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String type;

}
