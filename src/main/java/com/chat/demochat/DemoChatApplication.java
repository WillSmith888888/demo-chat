package com.chat.demochat;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.chat.demochat")
public class DemoChatApplication
{

    public static void main(String[] args)
    {
        if (args != null && args.length > 0)
        {
            log.info("启动参数：{}", JSON.toJSONString(args));
        }
        SpringApplication.run(DemoChatApplication.class, args);
    }

}
