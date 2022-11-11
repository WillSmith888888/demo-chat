package com.chat.demochat.listener;

import com.chat.demochat.entity.LoginInfo;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class LoginInfoCacheListener implements CacheEventListener<String, LoginInfo>
{
    @Override
    public void onEvent(CacheEvent<? extends String, ? extends LoginInfo> cacheEvent)
    {
        log.info(cacheEvent.getType() + " ---> " + cacheEvent.getKey() + " ---> " + cacheEvent.getNewValue());
    }
}
