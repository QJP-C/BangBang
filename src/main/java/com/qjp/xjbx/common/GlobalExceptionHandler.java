package com.qjp.xjbx.common;

import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.EOFException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j

public class GlobalExceptionHandler {
    /**
     * 捕获全局异常，处理所有不可知的异常
     */
    @ExceptionHandler(value=Exception.class)
    public R<String> handleException(Exception e, HttpServletRequest request) {
        log.error("出现未知异常 -> ", e);
        return R.error("出现未知异常！ -> "+e.getClass().getName());
    }
    @ExceptionHandler(value = JWTDecodeException.class)
    public R<String> jwtException(JWTDecodeException e){
        log.error("出现未知异常 -> ", e);
        return R.error("我劝你别瞎搞 O_o! 令牌格式有误！ -> "+e.getClass().getName());
    }
    @ExceptionHandler(value = EOFException.class)
    public R<String> EOFException(EOFException e){
        log.error("出现未知异常 -> ", e);
        return R.error("连接已断开！ -> "+e.getClass().getName());
    }
    /**
     * 捕获空指针异常
     */
    @ExceptionHandler(value=NullPointerException.class)
    public R<String> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("出现空指针异常 -> ", e);
        return R.error("出现空指针异常! -> "+e.getClass().getName());
    }
    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");//以空格分为数组
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误！");
    }

    /**
     * 自定义异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage() );
    }
}
