package com.chat.demochat.component;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;

@Slf4j
public class SessionWrapper
{

    private String account;

    public void setAccount(String account)
    {
        this.account = account;
    }

    // websocket session
    private Session session;

    public void setSession(Session session)
    {
        this.session = session;
    }

    // 单线程线程池
    private SingleThreadPoolExecutor singleThreadPoolExecutor;

    // 绑定单线程池
    public void setThreadPoolExecutor(SingleThreadPoolExecutor executor)
    {
        singleThreadPoolExecutor = executor;
    }

    public int reduceOneBind()
    {
        return singleThreadPoolExecutor.reduceOneBind();
    }

    public static final SessionWrapper bindSessionAndThreadPoolExecutor(String account, Session session, SingleThreadPool pool)
    {
        SessionWrapper wrapper = new SessionWrapper();
        wrapper.setAccount(account);
        wrapper.setSession(session);
        SingleThreadPoolExecutor executor = pool.getMinBindSingleThreadPoolExecutor();
        wrapper.setThreadPoolExecutor(executor);
        executor.addOneBind();
        log.info("用户[{}]-session[{}]绑定了线程池[{}]", account, session.getId(), executor.getName());
        return wrapper;
    }

    public final void unBind() throws IOException
    {
        this.session.close();
        this.singleThreadPoolExecutor.reduceOneBind();
        log.info("用户[{}]-session[{}]解绑了线程池[{}]", account, session.getId(), this.singleThreadPoolExecutor.getName());
    }

    public void close() throws IOException
    {
        this.session.close();
    }

    public void sendText(String msg)
    {
        singleThreadPoolExecutor.execute(() ->
        {
            try
            {
                log.info("用户[{}]发送信息:[{}]", this.account, msg);
                session.getBasicRemote().sendText(msg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                log.error("发送信息[{}]出现异常", msg, e);
            }
        });
    }

    public void sendObject(Object msg)
    {
        singleThreadPoolExecutor.execute(() ->
        {
            try
            {
                log.info("用户[{}]发送信息:[{}]", this.account, msg);
                session.getBasicRemote().sendObject(msg);
            }
            catch (IOException | EncodeException e)
            {
                e.printStackTrace();
                log.error("发送信息[{}]出现异常", msg, e);
            }
        });
    }


}
