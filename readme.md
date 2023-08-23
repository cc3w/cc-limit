### 限流实现过程



> `AOP` 拦截带有`@Limit` 注解的请求，判断相应的时间内请求的次数是否符合，不符合就做相应的逻辑处理，这里是抛出了一个异常，符合就继续放过此请求。
>
> `LimiterHandler` 类是用来指定具体使用哪个限流组件，这里默认使用的计数限流组件
>
> `CounterLimiter` 类是具体的计数限流实现类，这里采用的`HashMap` 和延迟队列（`BlockingQueue` ）两种数据结构来实现，`map` 用来存储key和访问次数以及一个限定的时间，为了方便这里的`key` 使用的是访问路径，当同一个`key` 的在一段时间内的访问次数超过规定的次数，就把这个`key` 和限定时间加入延迟队列，同时新开了一个线程，此线程在初始化`CounterLimiter` 类的时候就开始执行，此线程的目的就是从延迟队列中取出队头元素，调用的是它的`take()` 方法，此方法和`poll()` 方法的区别是：如果队列中没有元素，使用`take()` 方法它会一直阻塞，直到队列中有元素，使用`poll()` 方法它会返回`null` 。达到一个`key` 的可以请求的时间后，就可以从队列中取出元素，删除`map` 中对应的`key` ，此时该`key` 又可以重新访问





### 延迟队列的实现

> 需要实现`Delayed` 接口，实现其中的`getDelay()` 方法和`compareTo()` 方法，同时这里也必须实现`equals()` 方法和`hashCode()` 方法，重写equals方法时只比较`DataDelay` 的`key` 值；具体如下：



```java
private BolockingQueue<DataDelay> delayQueue = new DelayQueue<>();

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
            return unit.convert(this.expire - System.currentTimeMillis(), 	TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long f = this.getDelay(TimeUnit.MILLISECONDS) -
                o.getDelay(TimeUnit.MILLISECONDS);
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
```





### 新增令牌桶限流实现

> 主要利用了单线程定时任务执行器，这个执行定时任务的接口每隔一定时间往令牌桶里加token，每次请求就相当于往令牌桶取token，如果令牌桶此时的token数量小于0，那么它将拒绝请求。

```java
 private void init() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if(surplus.get() < capacity) {
                surplus.getAndAdd(rate);
            }
        }, 0, interval, TimeUnit.SECONDS);
    }
// intervalzhi间隔的时间
```

