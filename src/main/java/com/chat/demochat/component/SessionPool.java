package com.chat.demochat.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class SessionPool
{

    private static final ConcurrentHashMap<String, SessionWrapper> sessionPool = new ConcurrentHashMap<>();

    @Resource
    private SingleThreadPool singleThreadPool;

    public void bindSession(String account, Session session)
    {
        SessionWrapper wrapper = SessionWrapper.bindSessionAndThreadPoolExecutor(account, session, singleThreadPool);
        sessionPool.put(account, wrapper);
    }

    public boolean containsKey(String account)
    {
        return sessionPool.containsKey(account);
    }

    public void remove(String account) throws IOException
    {
        SessionWrapper wrapper = sessionPool.get(account);
        wrapper.unBind();
        sessionPool.remove(account);
    }

    public void sendText(final String account, final String msg) throws IOException
    {

        SessionWrapper wrapper = sessionPool.get(account);
        wrapper.sendText(msg);

        /*Session session = sessionPool.get(account);
        synchronized (session)
        {
            session.getBasicRemote().sendText(msg);
        }*/
    }

    public void info()
    {
        log.info("当前连接用户：{}", sessionPool.keySet());
    }
}
