package com.chat.demochat.controller;

import com.chat.demochat.exception.Resp;
import com.chat.demochat.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
public class FileController
{

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping(value = "/upload.do")
    public Object upload(@RequestParam("file") MultipartFile file, @RequestParam("password") String password) throws IOException
    {
        InputStream is = file.getInputStream();
        String originalFilename = file.getOriginalFilename();
        String zipName = originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".zip";
        log.info("源文件：{}, 生成文件全路径：{}", originalFilename, uploadPath + zipName);
        ZipUtil.compressedFileWithPassword(is, file.getOriginalFilename(), uploadPath + zipName, password);
        return Resp.getInstance("000000", null, zipName);
    }

}
