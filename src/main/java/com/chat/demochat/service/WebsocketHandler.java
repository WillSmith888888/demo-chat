package com.chat.demochat.service;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.anno.Mapped;
import com.chat.demochat.anno.MappedEnum;
import com.chat.demochat.component.SessionPool;
import com.chat.demochat.entity.*;
import com.chat.demochat.exception.AlreadyFriendException;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.NotExistAccountException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebsocketHandler
{
    private Map<String, MethodInfo> METHOD_MAP = new HashMap<>();

    @Resource
    private UserService userService;

    @Resource
    private SessionPool sessionPool;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    public WebsocketHandler()
    {
        Method[] methods = WebsocketHandler.class.getMethods();
        for (Method method : methods)
        {
            Mapped mapped = method.getAnnotation(Mapped.class);
            if (mapped != null)
            {
                log.info("[{}]---->[{}]", mapped.value(), method.getName());
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setMethod(method);
                methodInfo.setDest(mapped.dest());
                methodInfo.setClazz(method.getParameterTypes()[0]);
                METHOD_MAP.put(mapped.value(), methodInfo);
            }
        }
    }


    public void handle(WSRequest request) throws IllegalAccessException, InvocationTargetException, LoginException, IOException
    {
        WSResponse wsResponse = new WSResponse();
        wsResponse.setMapped(request.getMapped());
        LoginInfo loginInfo = cache.asMap().get(request.getToken());
        if (loginInfo == null)
        {
            throw new LoginException("000001", "用户已经失效");
        }
        MethodInfo method = METHOD_MAP.get(request.getMapped());
        if (method == null)
        {
            throw new IllegalAccessException("[" + request.getMapped() + "]不存在");
        }
        Class clazz = method.getClazz();
        Object parameter = JSON.parseObject(request.getBody(), clazz);
        Object result = method.getMethod().invoke(this, parameter);
        wsResponse.setData(result);
        if (method.getDest().equals(MappedEnum.WEB))
        {
            sessionPool.sendObject(loginInfo.getUser().getAccount(), wsResponse);
        }
    }

    @Mapped(value = "ping", dest = MappedEnum.WEB)
    public String ping(String ping)
    {
        log.info(ping);
        return "pong";
    }

    @Mapped(value = "send", dest = MappedEnum.KAFKA)
    public void send(MsgInfo req) throws LoginException
    {
        kafkaTemplate.send(req.getSessionId(), JSON.toJSONString(req));
    }

    @Mapped(value = "getSessionId", dest = MappedEnum.WEB)
    public String getSessionId(GetSessionIdReq req) throws LoginException
    {
        String sessionId = userService.getSessionId(req.getAccounts());
        return sessionId;
    }

    @Mapped(value = "addFriend", dest = MappedEnum.WEB)
    public User addFriend(AddFriendReq req) throws NotExistAccountException, AlreadyFriendException
    {
        User friend = userService.addFriend(req.getAccount(), req.getFriendAccount());
        return friend;
    }

    @Data
    public static class MethodInfo
    {
        private Method method;

        private MappedEnum dest;

        private Class clazz;
    }

}
