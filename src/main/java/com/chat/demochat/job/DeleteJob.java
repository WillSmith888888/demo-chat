package com.chat.demochat.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
public class DeleteJob
{

    @Value("${upload.path}")
    private String uploadPath;

    @Scheduled(cron = "0 0/5 * * * ?")
    private void delete() throws IOException
    {
        log.info("定时任务清理过期文件");
        File file = new File(uploadPath);
        File[] files = file.listFiles();
        for (File _file : files)
        {
            Path path = Paths.get(_file.getAbsolutePath());
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime fileTime = attrs.creationTime();
            long createTime = fileTime.toMillis();
            long currentTime = System.currentTimeMillis();
            if ((currentTime - createTime) > 3600000)
            {
                boolean deleted = _file.delete();
                if (deleted)
                {
                    log.info("文件[{}]被删除", _file.getAbsolutePath());
                }
            }
        }
    }
}
