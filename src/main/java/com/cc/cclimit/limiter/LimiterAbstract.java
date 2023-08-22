package com.cc.cclimit.limiter;

/**
 * @Author cc
 * @Date 2023/8/21 16:08
 * @PackageName:com.cc.cclimit.limiter
 * @ClassName: LimitAbstract
 * @Description: TODO
 * @Version 1.0
 */
public abstract class LimiterAbstract implements Limiter {


    @Override
    public void set(String key, Integer value, long time) {

    }

    @Override
    public Integer get(String key) {
        return null;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void incr(String key, long time) {

    }

}
