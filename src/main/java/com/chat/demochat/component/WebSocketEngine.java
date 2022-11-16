package com.chat.demochat.component;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.MsgInfo;
import com.chat.demochat.entity.MsgWrapper;
import com.chat.demochat.entity.User;
import com.chat.demochat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


@Slf4j
@ServerEndpoint("/engine/{token}")
@Component
public class WebSocketEngine
{

    @Resource
    private MessageConsumer consumer;

    @Resource
    private SessionPool sessionPool;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private UserService userService;

    @Resource
    private SingleThreadPool singleThreadPool;

    @Resource
    private CacheManager cacheManager;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    private static WebSocketEngine engine;

    @PostConstruct  //关键点3
    public void init()
    {
        engine = this;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token)
    {
        try
        {
            // 1.校验登录信息
            log.info("token:[{}]", token);
            LoginInfo loginInfo = engine.cache.asMap().get(token);
            if (loginInfo == null)
            {
                session.getAsyncRemote().sendText(MsgWrapper.wrap(1, "登录信息失效").toString());
                session.close();
                return;
            }
            session.getAsyncRemote().sendText(MsgWrapper.wrap(2, loginInfo.getUser()).toString());

            // 2.校验用户
            log.info("用户[{}]请求连接", loginInfo.getUser().getAccount());
            String account = loginInfo.getUser().getAccount();
            User user = engine.userService.get(account);
            if (user == null)
            {
                log.info("账号[{}]已经失效", account);
                session.getAsyncRemote().sendText(MsgWrapper.wrap(1, "当前用户已经失效").toString());
                session.close();
                return;
            }

            // 3.存储websocket session
            if (engine.sessionPool.containsKey(account))
            {
                log.info("用户[{}]在别处登录，关闭原有的连接");
                engine.sessionPool.remove(account);
            }
            engine.sessionPool.bindSession(account, session);
            log.info("用户[{}]连接成功", account);

            // 4.收集之前的信息
            engine.consumer.consumeBefore(account);
        }
        catch (Exception e)
        {
            log.error("用户[{}]连接出现异常", e);
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "token") String token)
    {
        try
        {
            LoginInfo loginInfo = engine.cache.asMap().get(token);
            engine.sessionPool.remove(loginInfo.getUser().getAccount());
            log.info("用户[{}]断开连接", loginInfo.getUser().getAccount());
        }
        catch (Exception e)
        {
            log.error("用户[{}]断开连接出现异常", e);
        }
    }


    @OnMessage
    public void onMessage(Session session, String msg)
    {
        log.info("【websocket消息】收到客户端消息:" + msg);
        if ("ping".equals(msg))
        {
            session.getAsyncRemote().sendText("pong");
        }
        else
        {
            MsgInfo msgInfo = JSON.parseObject(msg, MsgInfo.class);
            engine.kafkaTemplate.send(msgInfo.getSessionId(), msg);
        }
    }

    @OnError
    public void onError(Session session, Throwable error)
    {
        log.error("连接出现错误", error.getMessage());
        error.printStackTrace();
    }

}
