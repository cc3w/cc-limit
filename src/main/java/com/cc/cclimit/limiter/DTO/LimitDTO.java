package com.cc.cclimit.limiter.DTO;

/**
 * @Author cc
 * @Date 2023/8/21 15:30
 * @PackageName:com.cc.cclimit.limiter.DTO
 * @ClassName: LimitDTO
 * @Description: TODO
 * @Version 1.0
 */
public class LimitDTO {
    
    /**
     * 限制次数
     * **/
    
    public final int limit;

    /**
     * 限制时间
     */
    public final long time;

    /**
     * 用于不同具体限流实现
     */
    public final String key;

    public LimitDTO(int limit, long time, String key) {
        this.limit = limit;
        this.time = time;
        this.key = key;
    }
}
