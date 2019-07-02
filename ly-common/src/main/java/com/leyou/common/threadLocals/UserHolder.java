package com.leyou.common.threadLocals;

/**
 * 存放用户id的多线程池之类的map
 * @Author: dzw
 * @Date: 2019/7/2 20:40
 * @Version 1.0
 */
public class UserHolder {

    private static final ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void setUser(Long userId){

        TL.set(userId);
    }

    public static Long getUser(){
        return TL.get();
    }

    public static void removeUser(){
        TL.remove();
    }

}
