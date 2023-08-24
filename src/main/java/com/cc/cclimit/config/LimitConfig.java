package com.cc.cclimit.config;

import com.cc.cclimit.handler.LimiterHandlerDefined;
import com.cc.cclimit.limiter.impl.CounterLimiterRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author cc
 * @Date 2023/8/24 19:51
 * @PackageName:com.cc.cclimit.config
 * @ClassName: LimitConfig
 * @Description: TODO
 * @Version 1.0
 */
@Configuration
public class LimitConfig {

    @Bean
    @Primary
    public LimiterHandlerDefined limiterHandler() {
        return new LimiterHandlerDefined(new CounterLimiterRedis());
    }

}
