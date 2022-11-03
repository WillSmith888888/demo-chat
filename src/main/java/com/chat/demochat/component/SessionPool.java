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

    private static final ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    public void put(String account, Session session)
    {
        sessionPool.put(account, session);
    }

    public boolean containsKey(String account)
    {
        return sessionPool.containsKey(account);
    }

    public void remove(String account) throws IOException
    {
        sessionPool.remove(account);
        close(account);
    }

    public void sendText(final String account, final String msg) throws IOException
    {

        threadPoolExecutor.execute(() ->
        {
            Session session = sessionPool.get(account);
            synchronized (session)
            {
                try
                {
                    session.getBasicRemote().sendText(msg);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        /*Session session = sessionPool.get(account);
        synchronized (session)
        {
            session.getBasicRemote().sendText(msg);
        }*/
    }

    public void close(String account) throws IOException
    {
        sessionPool.get(account).close();
    }

    public void info()
    {
        log.info("当前连接用户：{}", sessionPool.keySet());
    }
}
