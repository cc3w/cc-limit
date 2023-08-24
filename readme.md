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

> 主要利用了单线程定时任务执行器，这个执行定时任务的接口每隔一定时间往令牌桶里加token，每次请求就相当于往令牌桶取`token`，如果令牌桶此时的token数量小于0，那么它将拒绝请求。

```java
 private void init() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if(surplus.get() < capacity) {
                surplus.getAndAdd(rate);
            }
        }, 0, interval, TimeUnit.SECONDS);
    }
// interval指间隔的时间
```





### 新增`redis` 计数器限流实现

> 主要利用`redis` 的`z-set` 有序集合，`z-set` 集合中存储的成员是由时间戳转换的字符串，分数是分钟级别的时间戳，每次请求都会去删掉当前时间点（分钟级别）之前的key，然后每次请求都会将`key` 集合的大小和`limit` 比较，如果是大于的关系，那么说明已经超出请求次数限制，否则小于，就将他加入集合。具体实现如下：

```java
  public boolean check(int limit) {
        //分钟级别的时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String text = simpleDateFormat.format(new Date());
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        try {
            //获取当前时间（分钟级别）
            long waitClearTime = simpleDateFormat.parse(text).getTime();
            zSetOperations.removeRangeByScore(KEY, 0, waitClearTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Long count = zSetOperations.zCard(KEY);
        if(count < limit) {
            //获取当前时间戳(毫秒级别)
            long time1 = new Date().getTime();
            zSetOperations.add(KEY, String.valueOf(time1), time1);
            return false;
        } else {
            return true;
        }
    }
```





### 新增滑动窗口限流实现

> 以时间为范围，滑动窗口就是某个时间范围，如果用户的请求在某个范围内超出了这个窗口规定的请求的最大值就拒绝访问。具体实现步骤：受到用户的请求每次都会去获取滑动窗口中的请求的数量，如果超出了最大值就拒绝访问，如果没有，先移动滑动窗口，再将用户请求加入滑动窗口中，并设置过期时间。这个滑动窗口用redis中z-set维护



### 新增漏斗限流实现

> 漏斗有个出水速率和进水速率，如果进水速率大于出水速率，那么随着时间的推移，水一定会从上面溢出来。具体实现：每次用当前漏斗中的剩余水量减去这段时间流走的水量（这段时间*出水速率），然后再去判断当前剩余水量加1是否大于漏斗容量，如果小于，就接受请求，否则拒绝请求。