package com.chat.demochat.conf;

import com.chat.demochat.component.WSThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ThreadPoolConfig
{

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor()
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Integer.MAX_VALUE), new WSThreadFactory(), (r, executor1) ->
        {
            log.info("忽略该任务");
        });

        return executor;
    }
}
