package com.cc.cclimit.controller;

import com.cc.cclimit.annotation.Limit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author cc
 * @Date 2023/8/21 15:03
 * @PackageName:com.cc.cclimit.controller
 * @ClassName: LimiterController
 * @Description: TODO
 * @Version 1.0
 */

@RestController
public class LimiterController {

    @RequestMapping("/test1")
    @Limit(limit = 5, time = 5, msg = "计数器的")
    public String test1() {
        //System.out.println("进入aop切面拦截·····");
        return "test1";
    }

    @RequestMapping("/test2")
    @Limit(limit = 2, time = 5)
    public String test2() {
        return "test2";
    }

}
