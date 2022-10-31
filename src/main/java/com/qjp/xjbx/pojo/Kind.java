package com.qjp.xjbx.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务具体类
 * @author qjp
 */
@Data
public class Kind implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String classId;
    private String name;
}
