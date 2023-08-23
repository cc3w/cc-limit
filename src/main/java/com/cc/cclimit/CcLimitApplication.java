package com.cc.cclimit;

import com.cc.cclimit.handler.LimiterHandler;
import com.cc.cclimit.limiter.impl.TokenBucketLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CcLimitApplication {

    public static void main(String[] args) {
        LimiterHandler.setLimiter(TokenBucketLimiter.getInstance());
        SpringApplication.run(CcLimitApplication.class, args);
    }

}
