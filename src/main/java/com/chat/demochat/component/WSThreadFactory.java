package com.chat.demochat.component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WSThreadFactory implements ThreadFactory
{

    // 将创建的线程保存在
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public WSThreadFactory()
    {
        @SuppressWarnings("removal") SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public WSThread newThread(Runnable r)
    {
        WSThread t = new WSThread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
        {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY)
        {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
