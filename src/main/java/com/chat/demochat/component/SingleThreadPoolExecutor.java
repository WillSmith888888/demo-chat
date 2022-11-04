package com.chat.demochat.component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadPoolExecutor extends ThreadPoolExecutor
{
    private String name;

    private AtomicInteger atomicInteger;

    public int addOneBind()
    {
        return atomicInteger.getAndIncrement();
    }

    public int getCurrentNum()
    {
        return atomicInteger.get();
    }

    public String getName()
    {
        return name;
    }

    public SingleThreadPoolExecutor(String name, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
    {

        super(1, 1, keepAliveTime, unit, workQueue, threadFactory, handler);
        atomicInteger = new AtomicInteger(0);
        this.name = name;
    }

}
