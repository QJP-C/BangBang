package com.qjp.bang.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果
 * @author qjp
 * @param <T>
 */
@Data
public class R<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String message; //错误信息

    private T result; //数据

//    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.result = object;
        r.code = 1;
        return r;
    }

//    public static <T> R<T> success(T object,Map map) {
//        R<T> r = new R<T>();
//        r.result = object;
//        r.code = 1;
//        r.map = map;
//        return r;
//    }

    public static <T> R<T> success(T object,String message) {
        R<T> r = new R<T>();
        r.result = object;
        r.code = 1;
        r.message=message;
        return r;
    }
//    public static <T> R<T> success(T object,String message,Map map) {
//        R<T> r = new R<T>();
//        r.result = object;
//        r.code = 1;
//        r.message=message;
//        r.map = map;
//        return r;
//    }

    public static <T> R<T> success(String message) {
        R<T> r = new R<T>();
        r.code = 1;
        r.message = message;
        return r;
    }

    public static <T> R<T> error(String message) {
        R<T> r = new R<T>();
        r.message = message;
        r.code = 0;
        return r;
    }
    public static <T> R<T> error(Integer code,String message) {
        R<T> r = new R<T>();
        r.message = message;
        r.code = code;
        return r;
    }

//    public R<T> add(String key, Object value) {
//        this.map.put(key, value);
//        return this;
//    }

}
