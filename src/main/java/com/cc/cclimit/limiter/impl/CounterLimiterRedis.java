package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author cc
 * @Date 2023/8/23 10:47
 * @PackageName:com.cc.cclimit.limiter.impl
 * @ClassName: CounterLimiterRedis
 * @Description: TODO
 * @Version 1.0
 */

public class CounterLimiterRedis extends LimiterAbstract {


    RedisTemplate redisTemplate;

    static final String KEY = "COUNTER_LIMITER:";

    public CounterLimiterRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }



    @Override
    public boolean check(LimitDTO limiterDTO) {
        int limit = limiterDTO.limit;
        return check(limit);
    }

    public boolean check(int limit) {
        return false;
    }

}
