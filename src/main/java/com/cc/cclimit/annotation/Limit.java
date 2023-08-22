package com.cc.cclimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author cc
 * @Date 2023/8/21 15:06
 * @PackageName:com.cc.cclimit.annotation
 * @ClassName: Limit
 * @Description: TODO
 * @Version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    int limit() default 0;

    int time() default 0;

    String key() default "";

    String msg() default "系统服务繁忙";
}
