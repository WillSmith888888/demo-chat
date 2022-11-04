package com.chat.demochat.component;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.entity.MsgInfo;
import com.chat.demochat.entity.User;
import com.chat.demochat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@ServerEndpoint("/engine/{account}/{friends}")
@Component
public class WebSocketEngine
{

    private static final String TOPIC_PREFIX = "websocket.";

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

    private static WebSocketEngine engine;

    @PostConstruct  //关键点3
    public void init()
    {
        engine = this;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "account") String account, @PathParam(value = "friends") String friends)
    {
        try
        {

            log.info("用户[{}]请求连接", account);

            // 1. TODO 校验用户
            User user = engine.userService.get(account);
            if (user == null)
            {
                session.getAsyncRemote().sendText("000001");
                session.close();
            }

            // 2.建立连接存储session
            if (engine.sessionPool.containsKey(account))
            {
                log.info("用户[{}]在别处登录，关闭原有的连接");
                engine.sessionPool.remove(account);
            }
            engine.sessionPool.bindSession(account, session);
            log.info("用户[{}]连接成功", account);

            // 3.创建session
            List<String> accounts = Arrays.asList(account.concat(",").concat(friends).split(","));
            String sessionId = engine.userService.createSession(accounts);
            Map<String, String> accountNameMap = new HashMap<>();
            for (String _account : accounts)
            {
                User _user = engine.userService.get(_account);
                accountNameMap.put(_account, _user.getName());
            }
            engine.sessionPool.sendText(account, sessionId + "<--->" + JSON.toJSONString(user) + "<--->" + JSON.toJSONString(accountNameMap));

            // 4.收集之前的信息
            engine.consumer.consumeBefore(sessionId);
        }
        catch (Exception e)
        {
            log.error("用户[{}]连接出现异常", e);
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "account") String account)
    {
        try
        {
            engine.sessionPool.remove(account);
            log.info("用户[{}]断开连接", account);
        }
        catch (Exception e)
        {
            log.error("用户[{}]断开连接出现异常", e);
        }
    }


    @OnMessage
    public void onMessage(String msg)
    {
        log.info("【websocket消息】收到客户端消息:" + msg);
        MsgInfo msgInfo = JSON.parseObject(msg, MsgInfo.class);
        engine.kafkaTemplate.send(msgInfo.getSessionId(), msg);
    }

    @OnError
    public void onError(Session session, Throwable error)
    {
        log.error("连接出现错误", error.getMessage());
        error.printStackTrace();
    }

}
