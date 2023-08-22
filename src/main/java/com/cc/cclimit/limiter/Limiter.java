package com.cc.cclimit.limiter;

import com.cc.cclimit.limiter.DTO.LimitDTO;

/**
 * @Author cc
 * @Date 2023/8/21 15:26
 * @PackageName:com.cc.cclimit.limiter
 * @ClassName: Limiter
 * @Description: TODO
 * @Version 1.0
 */
public interface Limiter {

    /**
     * @author cc
     * @date 2023/8/21 15:42
     * @params [key, value, time] 
     * @return [java.lang.String, java.lang.Integer, long]
     * 增加
     * **/
    
    void set(String key, Integer value, long time);

    /**
     * @author cc
     * @date 2023/8/21 15:43
     * @params [] 
     * @return []
     * 获取，可能为空
     * **/
    
    Integer get(String key);

    /**
     * @author cc
     * @date 2023/8/21 15:50
     * @params [key]
     * @return [java.lang.String]
     * 删除
     * **/

    void remove(String key);

    /**
     * @author cc
     * @date 2023/8/21 15:50
     * @params [key, time]
     * @return [java.lang.String, long]
     * 自增
     * **/

    void incr(String key, long time);

    /**
     * @author cc
     * @date 2023/8/21 15:50
     * @params [limiterDTO]
     * @return [com.cc.cclimit.limiter.DTO.LimitDTO]
     * 检查是否达到限制，子类必须强制实行该接口
     * **/

    boolean check(LimitDTO limiterDTO);

}
