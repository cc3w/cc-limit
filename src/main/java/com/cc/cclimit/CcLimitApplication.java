package com.cc.cclimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CcLimitApplication {

    public static void main(String[] args) {
        //LimiterHandler.setLimiter(new CounterLimiterRedis(new RedisTemplate<>()));
        SpringApplication.run(CcLimitApplication.class, args);
    }

}
