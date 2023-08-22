package com.cc.cclimit.aop;

import com.cc.cclimit.annotation.Limit;
import com.cc.cclimit.handler.LimiterHandler;
import com.cc.cclimit.limiter.DTO.LimitDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author cc
 * @Date 2023/8/21 16:27
 * @PackageName:com.cc.cclimit.aop
 * @ClassName: LimiterAop
 * @Description: TODO
 * @Version 1.0
 */
@Component
@Aspect
public class LimiterAop {
    @Autowired
    HttpServletRequest request;

    @Autowired
    LimiterHandler limiterHandler;

    @Pointcut("@annotation(com.cc.cclimit.annotation.Limit)")
    public void aopPoint() {

    }

    /**
     * @author cc
     * @date 2023/8/21 16:36
     * @params  
     * @return 
     * @desc 拦截limit注解的请求
     * **/

//    @Before("aopPoint()")
//    public void testAop() throws Throwable {
//        System.out.println("测试aop成功····");
//
//    }


    @Around("aopPoint() && @annotation(limiter)")
    public Object restriction(ProceedingJoinPoint joinPoint, Limit limiter) throws Throwable {
        String key = request.getRequestURI();
        int limit = limiter.limit();
        long time = limiter.time();
        LimitDTO limitDTO = new LimitDTO(limit, time, key);

        //System.out.println("aop切面拦截·····key的值是 + " + key);

        boolean result = limiterHandler.check(limitDTO);

        if (result) {
            throw new RuntimeException(limiter.msg());
        }
        Object o = joinPoint.proceed();
        return o;
    }

}
