package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author cc
 * @Date 2023/8/23 17:51
 * @PackageName:com.cc.cclimit.limiter.impl
 * @ClassName: SlideWindowsLimiter
 * @Description: TODO
 * @Version 1.0
 */
@Component
public class SlideWindowsLimiter extends LimiterAbstract {

    @Resource
    private RedisTemplate<String, String> redis;

    public static RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void getRedisTemplate() {
        redisTemplate = this.redis;
    }


    private static int interval = 10;

    private static int window = 5;

    public SlideWindowsLimiter() {

    }


    @Override
    public boolean check(LimitDTO limiterDTO) {
        return check(interval, window);
    }

    static final String SLIDE_WINDOW = "SLIDE_WINDOW";

    public boolean check(int interval, int window) {
        String key = SLIDE_WINDOW;
        //System.out.println("redisTemplate " + redisTemplate);
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Long count = zSetOperations.zCard(key);
        if(count < window) {
            increment(key, interval);
            return false;
        }
        return true;
    }

    public void increment(String key, Integer interval) {
        long currentMs = System.currentTimeMillis();

        long windowStartMs = currentMs - interval * 1000;

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.removeRangeByScore(key, 0, windowStartMs);

        zSetOperations.add(key, String.valueOf(currentMs), currentMs);

        redisTemplate.expire(key, interval, TimeUnit.SECONDS);

    }
}
