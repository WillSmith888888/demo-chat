package com.chat.demochat.controller;

import com.chat.demochat.exception.Resp;
import com.chat.demochat.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "文件")
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

    @PostMapping(value = "/common/file.do")
    public Object upload(@RequestParam("file") @RequestPart @ApiParam("file") MultipartFile file) throws IOException
    {
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        InputStream is = file.getInputStream();
        String fileName = UUID.randomUUID() + fileType;
        String filePath = uploadPath + fileName;
        log.info("文件上传路径:{}", filePath);
        FileCopyUtils.copy(is, Files.newOutputStream(Paths.get(filePath)));
        return Resp.getInstance("000000", null, fileName);
    }

}
