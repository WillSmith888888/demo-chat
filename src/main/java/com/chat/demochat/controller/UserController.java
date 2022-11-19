package com.chat.demochat.controller;

import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.Notify;
import com.chat.demochat.entity.User;
import com.chat.demochat.exception.AlreadyFriendException;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.NotExistAccountException;
import com.chat.demochat.exception.Resp;
import com.chat.demochat.service.UserService;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "用户管理")
public class UserController
{

    @Value("${img.path}")
    private String imgPath;

    @Resource
    private UserService userService;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    @ApiOperation(value = "创建用户接口")
    @PostMapping(value = "/createUser.do", headers = "content-type=multipart/form-data")
    public Object createUser(@RequestParam(value = "file") @RequestPart @ApiParam("file") MultipartFile file, @RequestParam("account") String account, @RequestParam("name") String name, @RequestParam("password") String password, String friends) throws IOException
    {
        User user = new User();
        user.setAccount(account);
        user.setName(name);
        user.setPassword(password);
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

}
