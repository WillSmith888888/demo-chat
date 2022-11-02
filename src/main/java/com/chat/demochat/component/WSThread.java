package com.chat.demochat.component;


import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSThread extends Thread
{
    private ConcurrentHashMap<String, Session> concurrentHashMap = new ConcurrentHashMap();

    public Map<String, Session> getMap()
    {
        return concurrentHashMap;
    }

    public WSThread(ThreadGroup group, Runnable target, String name,
                    long stackSize)
    {
        super(group, target, name, stackSize);
    }


}
