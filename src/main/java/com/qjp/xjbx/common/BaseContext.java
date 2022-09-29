package com.qjp.xjbx.common;

/**
 * 基于ThreadLocal封装的工具类，用户保存和获取当前用户的id   （作用域为该线程内）
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static String getCurrentId(){
        return String.valueOf(threadLocal.get());
    }
}
