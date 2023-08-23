package com.cc.cclimit.limiter.impl;

import com.cc.cclimit.limiter.DTO.LimitDTO;
import com.cc.cclimit.limiter.LimiterAbstract;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author cc
 * @Date 2023/8/21 16:15
 * @PackageName:com.cc.cclimit.limiter.impl
 * @ClassName: CounterLimiter
 * @Description: TODO
 * @Version 1.0
 */
public class CounterLimiter extends LimiterAbstract {

    private Map<String, Data> map = new HashMap<>();
    private BlockingQueue<DataDelay> delayQueue = new DelayQueue<>();
    private boolean state = false;

    private int cnt = 0;

    private static CounterLimiter instance;

    public static CounterLimiter getInstance() {
        if(instance == null)
            return new CounterLimiter();
        return instance;
    }


    private CounterLimiter() {
        init();
    }


    @Override
    public void set(String key, Integer value, long time) {
        map.put(key, new Data(value, time));
        delayQueue.add(new DataDelay(key, time));
    }

    @Override
    public Integer get(String key) {
        return map.get(key).value.get();
    }

    @Override
    public void remove(String key) {
        map.remove(key);
        delayQueue.remove(new DataDelay(key));
    }

    @Override
    public void incr(String key, long time) {
        if(map.containsKey(key)) {
            map.get(key).incr();
        } else {
            set(key, 1, time);
        }
    }



    @Override
    public boolean check(LimitDTO limiterDTO) {
        //System.out.println("进入到CounterLimiter中来了····");
        String key = limiterDTO.key;
        int limit = limiterDTO.limit;
        long time = limiterDTO.time;
        if(!map.containsKey(key)) {
            set(key, 1, time);
            cnt ++;
//            System.out.println("map和delayQueue的值是" + map + "  ");
//            System.out.println("key的值是" + key);
            System.out.println("cnt的值是" + cnt + "map的大小是" + map.size());
//            Set<Map.Entry<String, Data>> entries = map.entrySet();
//            for(Map.Entry<String, Data> entry : entries)
//                System.out.println("map中key的值是" + entry.getKey());

            return false;
        }

        Data data = map.get(key);
        boolean res = data.value.get() >= limit;
        System.out.println("res的值是 + " + res + "value的值是 +" + data.value.get());
        if (res) {
            if(data.value.get() == limit) {
                removeDelayKey(key);
                addDelay(key, data.time);
            }
        }
        incr(key, time);
        return res;
    }

    private void removeDelayKey(String key) {
        delayQueue.remove(new DataDelay(key));
    }

    private void addDelay(String key, long time) {
        delayQueue.add(new DataDelay(key, time));
    }

    private void init() {
        new Thread(() -> {
            while(true) {
                try {
                    DataDelay take = delayQueue.take();
                    //System.out.println("map的大小是" + map.size());
                    map.remove(take.key);
                    //System.out.println("这是次线程···key的值是" + take.key + "map的大小是" + map.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CounterLimiter").start();
    }

    private class Data{
        AtomicInteger value;
        long time;

        public Data() {

        }

        public Data(Integer value, long time) {
            this.value = new AtomicInteger(value);
            this.time = time;
        }

        public void incr() {
            value.getAndIncrement();
        }
    }

    private class DataDelay implements Delayed {
        String key;

        long expire;

        public DataDelay(String key, long time) {
            this.key = key;
            this.expire = new Date().getTime() + (time * 1000);
        }

        public DataDelay(String key) {
            this.key = key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long f = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            return (int)f;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;
            if(obj == null || getClass() != obj.getClass())
                return false;
            DataDelay dataDelay = (DataDelay) obj;
            return Objects.equals(key, dataDelay.key);
        }
    }

}
