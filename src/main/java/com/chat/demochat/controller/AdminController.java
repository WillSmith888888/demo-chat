package com.chat.demochat.controller;

import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.User;
import com.chat.demochat.exception.Resp;
import com.chat.demochat.service.UserService;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "管理员")
public class AdminController
{
    @Value("${img.path}")
    private String imgPath;

    @Value("${upload.path}")
    private String uploadPath;

    @Resource
    private UserService userService;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    @ApiOperation(value = "创建用户接口")
    @PostMapping(value = "/createUser.do", headers = "content-type=multipart/form-data")
    public Object createUser(@RequestParam("account") String account, @RequestParam("name") String name, @RequestParam("password") String password, String friends, @RequestParam("logo") String logo) throws IOException
    {
        User user = new User();
        user.setAccount(account);
        user.setName(name);
        user.setPassword(password);
        user.setLogo(logo);
        List<User> list = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(friends))
        {
            for (String friend : friends.split(","))
            {
                User _user = new User();
                _user.setAccount(friend);
                list.add(_user);
            }
        }
        user.setFriends(list);
        File uploadFile = new File(uploadPath + logo);
        if (uploadFile.exists())
        {
            FileCopyUtils.copy(uploadFile, new File(imgPath + logo));
            return Resp.getInstance("000000", userService.createUser(user));
        }
        else
        {
            return Resp.getInstance("000008", "距离上传logo时间太长，logo已经自动删除");
        }
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

}
