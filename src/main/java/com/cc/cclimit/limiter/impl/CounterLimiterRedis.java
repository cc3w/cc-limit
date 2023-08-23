package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        //分钟级别的时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String text = simpleDateFormat.format(new Date());
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        try {
            long waitClearTime = simpleDateFormat.parse(text).getTime();
            zSetOperations.removeRangeByScore(KEY, 0, waitClearTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Long count = zSetOperations.zCard(KEY);
        if(count < limit) {
            //获取当前时间戳
            long time1 = new Date().getTime();
            zSetOperations.add(KEY, String.valueOf(time1), time1);
            return false;
        } else {
            return true;
        }
    }

}
