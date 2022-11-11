package com.chat.demochat.controller;

import com.chat.demochat.entity.User;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.Resp;
import com.chat.demochat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class UserController
{

    @Value("${img.path}")
    private String imgPath;

    @Resource
    private UserService userService;

    @PostMapping(value = "/createUser.do")
    public Object createUser(@RequestParam("file") MultipartFile file, @RequestParam("account") String account, @RequestParam("name") String name, @RequestParam("password") String password) throws IOException
    {
        User user = new User();
        user.setAccount(account);
        user.setName(name);
        user.setPassword(password);
        userService.createUser(user);
        int i = file.getOriginalFilename().lastIndexOf(".");
        String fileType = file.getOriginalFilename().substring(i);
        InputStream is = file.getInputStream(); //文件输入流
        log.info("文件路径：{}", imgPath + account + fileType);
        OutputStream os = new FileOutputStream(new File(imgPath + account + fileType));
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) != -1)
        {
            os.write(buffer, 0, len);
            os.flush();
        }
        os.close();
        is.close();
        return "000000";
    }


    @GetMapping(value = "/query.do")
    public Object query(String account)
    {
        Object o = userService.get(account);
        return o != null ? o : "用户不存在";
    }

    @GetMapping(value = "/delete.do")
    public Object delete(String account)
    {
        userService.delByAccount(account);
        return "删除成功";
    }

    @PostMapping(value = "/login.do")
    public Object login(String account, String password)
    {
        Resp resp;
        try
        {
            resp = userService.login(account, password);
        }
        catch (LoginException e)
        {
            e.printStackTrace();
            log.error("用户登录出现异常：", e);
            resp = Resp.getInstance(e.getCode(), e.getMsg());
        }
        return resp;
    }

}
