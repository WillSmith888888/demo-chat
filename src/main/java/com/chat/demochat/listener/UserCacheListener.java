package com.chat.demochat.listener;

import com.chat.demochat.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class UserCacheListener implements CacheEventListener<String, User>
{

    @Override
    public void onEvent(CacheEvent<? extends String, ? extends User> cacheEvent)
    {
        log.info(cacheEvent.getType() + " ---> " + cacheEvent.getKey() + " ---> " + cacheEvent.getNewValue());
    }
}
