package com.qjp.xjbx.common;

/**
 * 自定义业务异常
 * @author qjp
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
