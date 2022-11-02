package com.chat.demochat.service.impl;

import com.chat.demochat.cons.Constant;
import com.chat.demochat.entity.User;
import com.chat.demochat.service.UserService;
import com.chat.demochat.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{

    private static final String TOPIC_PREFIX = "websocket.";

    @Resource
    private Consumer<String, String> consumer;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void createUser(User user)
    {
        Cache userCache = cacheManager.getCache("userCache");
        userCache.put(user.getAccount(), user);
    }


    @Override
    public User get(String account)
    {
        Cache userCache = cacheManager.getCache("userCache");
        User user = userCache.get(account, User.class);
        return user;
    }


    @Override
    public String createSession(List<String> accounts)
    {
        // 1.根据用户算出sessionId
        String sessionId = Utils.getSessionId(accounts);

        // 2.判断sessionId是否存在
        Map<String, List<PartitionInfo>> topics = consumer.listTopics();
        boolean bool = topics.containsKey(sessionId);

        // 3.将sessionId存入到kafka
        if (!bool)
        {
            for (String account : accounts)
            {
                kafkaTemplate.send(Constant.USER_TOPIC_PREFIX.concat(account), sessionId);
            }
        }

        return sessionId;
    }

    public static void main(String[] args)
    {
        TreeSet<String> treeSet = new TreeSet();
        treeSet.add("134");
        treeSet.add("347781");
        treeSet.add("189");
        treeSet.add("144");
        treeSet.add("5671");
        log.info(treeSet.toString());
    }
}
