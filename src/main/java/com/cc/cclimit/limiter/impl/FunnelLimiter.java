package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;

/**
 * @Author cc
 * @Date 2023/8/24 15:44
 * @PackageName:com.cc.cclimit.limiter.impl
 * @ClassName: FunnelLimiter
 * @Description: TODO
 * @Version 1.0
 */
public class FunnelLimiter extends LimiterAbstract {

    private long timeStamp = System.currentTimeMillis();
    //桶的容量10滴
    private long capacity = 10;
    //水漏出的速度 滴/s
    private double rate = 0.25;

    //当前水量10滴
    private long water = 5;

    @Override
    public synchronized boolean check(LimitDTO limiterDTO) {

        long now = System.currentTimeMillis();
        water = (long) Math.ceil(Math.max(0, water - (now - timeStamp) / 1000 * rate));
        if(water + 1 <= capacity) {
            water ++;
            System.out.println("water = " + water);
            return false;
        }
        return true;
    }
}
