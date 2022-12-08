package com.chat.demochat.controller;

import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.User;
import com.chat.demochat.exception.*;
import com.chat.demochat.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "用户")
public class UserController
{

    @Value("${img.path}")
    private String imgPath;

    @Value("${upload.path}")
    private String uploadPath;

    @Resource
    private UserService userService;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    @PostMapping(value = "/login.do")
    public Object login(String account, String password)
    {
        String token;
        try
        {
            token = userService.login(account, password);
        }
        catch (LoginException e)
        {
            e.printStackTrace();
            log.error("用户登录出现异常：", e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
        return Resp.getInstance("000000", null, token);
    }

    @PostMapping(value = "/logout.do")
    public Object logout(String token)
    {
        try
        {
            userService.logout(token);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("退出登录失败：", e);
            return Resp.getInstance("000005", "退出登录失败");
        }
        return Resp.getInstance("000000", "退出登录成功");
    }

    @PostMapping(value = "/getSessionId.do")
    public Object getSessionId(String accounts)
    {
        try
        {
            String sessionId = userService.getSessionId(accounts);
            log.info("获取到sessionId:[{}]", sessionId);
            return Resp.getInstance("000000", null, sessionId);
        }
        catch (LoginException e)
        {
            e.printStackTrace();
            log.error("获取会话ID出现异常:", e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
    }

    @PostMapping(value = "/addFriend.do")
    public Object addFriend(String token, String account)
    {
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            return Resp.getInstance("000004", "登录信息失效");
        }
        try
        {
            User friend = userService.addFriend(loginInfo.getUser().getAccount(), account);
            return Resp.getInstance("000000", "添加朋友成功", friend);
        }
        catch (NotExistAccountException e)
        {
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
        catch (AlreadyFriendException e)
        {
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
    }

    @PostMapping(value = "/removeFriend.do")
    public Object removeFriend(String token, String account)
    {
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            return Resp.getInstance("000004", "登录信息失效");
        }
        userService.removeFriend(loginInfo.getUser().getAccount(), account);
        return Resp.getInstance("000000", "删除朋友[" + account + "]成功");
    }

    @PostMapping(value = "/createGroupChat.do")
    public Object createGroupChat(String groupName, String accounts, String logo) throws IOException
    {
        try
        {
            File uploadFile = new File(uploadPath + logo);
            if (uploadFile.exists())
            {
                FileCopyUtils.copy(uploadFile, new File(imgPath + logo));
                return Resp.getInstance("000000", userService.createGroupChat(groupName, accounts, logo));
            }
            else
            {
                return Resp.getInstance("000008", "距离上传logo时间太长，logo已经自动删除");
            }
        }
        catch (LoginException e)
        {
            log.error(e.getMsg(), e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
        catch (AlreadyGroupException e)
        {
            log.error(e.getMsg(), e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
    }

    @PostMapping(value = "/getGroupChat.do")
    public Object getGroupChat(String sessionId)
    {
        try
        {
            return Resp.getInstance("000000", userService.getGroupChat(sessionId));
        }
        catch (LoginException e)
        {
            log.error(e.getMsg(), e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
    }

    @PostMapping(value = "/delGroupChat.do")
    public Object delGroupChat(String sessionId)
    {
        try
        {
            userService.delGroupChat(sessionId);
            return Resp.getInstance("000000", "会话删除成功");
        }
        catch (LoginException e)
        {
            log.error(e.getMsg(), e);
            return Resp.getInstance(e.getCode(), e.getMsg());
        }
    }

}
