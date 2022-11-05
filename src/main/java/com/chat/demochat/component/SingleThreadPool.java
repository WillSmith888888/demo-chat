package com.chat.demochat.component;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class SingleThreadPool
{
    private List<SingleThreadPoolExecutor> list;

    public SingleThreadPool(int num)
    {
        list = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            SingleThreadPoolExecutor singleThreadPoolExecutor = new SingleThreadPoolExecutor("pool-" + i, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new WSThreadFactory(i + ""), (r, executor) -> log.info("忽略该执行"));
            list.add(singleThreadPoolExecutor);
        }
        log.info("线程池个数:{}", list.size());
    }

    // 获取绑定数据最少的线程池
    public SingleThreadPoolExecutor getMinBindSingleThreadPoolExecutor()
    {
        list.sort(new Comparator<SingleThreadPoolExecutor>()
        {
            @Override
            public int compare(SingleThreadPoolExecutor o1, SingleThreadPoolExecutor o2)
            {
                return o1.getCurrentNum() - o2.getCurrentNum();
            }
        });
        for (SingleThreadPoolExecutor executor : list)
        {
            log.info("线程池[{}]绑定的session个数[{}]", executor.getName(), executor.getCurrentNum());
        }
        SingleThreadPoolExecutor executor = list.get(0);
        return executor;
    }

}
