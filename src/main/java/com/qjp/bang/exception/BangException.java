package com.qjp.bang.exception;

/**
 * 自定义业务异常
 * @author qjp
 */
public class BangException extends RuntimeException{
    private String errMessage;
    public BangException() {super();}
    public BangException(String message){
        super(message);
        System.out.println(message);
    }
    public static void cast(String message){
        throw new BangException(message);
    }
    public static void cast(CommonError error){
        throw new BangException(error.getErrMessage());
    }
}
