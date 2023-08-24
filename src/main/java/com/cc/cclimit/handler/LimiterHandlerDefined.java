package com.cc.cclimit.handler;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.Limiter;
import org.springframework.stereotype.Component;

/**
 * @Author cc
 * @Date 2023/8/21 16:12
 * @PackageName:com.cc.cclimit.handler
 * @ClassName: LimiterHandler
 * @Description: TODO
 * @Version 1.0
 */

@Component
public class LimiterHandlerDefined implements Limiter {

    //static Limiter limiter = CounterLimiter.getInstance();
    private Limiter limiter;


    public LimiterHandlerDefined(Limiter r) {
        this.limiter = r;
    }

    @Override
    public void set(String key, Integer value, long time) {
        limiter.set(key, value, time);
    }

    @Override
    public Integer get(String key) {
        return limiter.get(key);
    }

    @Override
    public void remove(String key) {
        limiter.remove(key);
    }

    @Override
    public void incr(String key, long time) {
        limiter.incr(key, time);
    }

    @Override
    public boolean check(LimitDTO limiterDTO) {
        //System.out.println("进入到limiterhandler中来了····" + limiter);
        return limiter.check(limiterDTO);
    }

}
