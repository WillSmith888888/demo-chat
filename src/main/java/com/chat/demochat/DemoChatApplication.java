package com.chat.demochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages="com.chat.demochat")
public class DemoChatApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(DemoChatApplication.class, args);
    }

}
