package com.chat.demochat.conf;

import com.chat.demochat.component.SingleThreadPool;
import com.chat.demochat.component.WSThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ThreadPoolConfig
{

    @Bean
    public SingleThreadPool getSingleThreadPool()
    {
        return new SingleThreadPool(5);
    }
}
