package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author cc
 * @Date 2023/8/23 11:05
 * @PackageName:com.cc.cclimit.limiter.impl
 * @ClassName: TokenBucketLimiter
 * @Description: TODO
 * @Version 1.0
 */
public class TokenBucketLimiter extends LimiterAbstract {

    // 桶容量
    private Integer capacity = 5;

    //每次放多少token
    private Integer rate = 1;

    // 间隔时间
    private Integer interval = 2;


    //单线程的定时任务执行器
    // ScheduledExectorService是用于执行定时任务的接口
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    // 剩余的token
    private AtomicInteger surplus = new AtomicInteger(capacity);

    private static TokenBucketLimiter instance;

    public static synchronized TokenBucketLimiter getInstance() {
        if(instance == null)
            return new TokenBucketLimiter();
        return instance;
    }

    private TokenBucketLimiter() {
        init();
    }


    @Override
    public boolean check(LimitDTO limiterDTO) {
        //System.out.println("进入到TokenBucketLimiter·····");
        if(surplus.get() <= 0)
            return true;
        //surplus.getAndIncrement();
        surplus.getAndDecrement();
        return false;
    }

    private void init() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if(surplus.get() < capacity) {
                surplus.getAndAdd(rate);
            }
        }, 0, interval, TimeUnit.SECONDS);
    }
}
