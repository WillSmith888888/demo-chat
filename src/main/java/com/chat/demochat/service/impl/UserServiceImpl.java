package com.chat.demochat.service.impl;

import com.chat.demochat.cons.Constant;
import com.chat.demochat.dao.UserRepository;
import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.User;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.Resp;
import com.chat.demochat.service.UserService;
import com.chat.demochat.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

@Transactional
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

    @Resource
    private UserRepository userRepository;


    @Override
    public void createUser(User user)
    {
//        Cache userCache = cacheManager.getCache("userCache");
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
//        userCache.put(user.getAccount(), user);
        userRepository.save(user);

    }


    @Override
    public User get(String account)
    {
//        Cache userCache = cacheManager.getCache("userCache");
//        User user = userCache.get(account, User.class);
        User user = userRepository.getReferenceById(account);
        return user;
    }

    @Override
    public void delByAccount(String account)
    {
        userRepository.deleteById(account);
    }

    @Override
    public Resp login(String account, String password) throws LoginException
    {
        User user = userRepository.getReferenceById(account);
        if (user == null)
        {
            log.info("用户[{}]已经失效", account);
            throw new LoginException("000001", "用户已经失效");
        }
        if (!user.getPassword().equals(DigestUtils.md5Hex(password)))
        {
            log.info("用户[{}]登录密码不正确", account);
            throw new LoginException("000003", "登录密码不正确");
        }
        Cache loginInfoCache = cacheManager.getCache("loginInfoCache");
        String token = String.valueOf(UUID.randomUUID());
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUser(user);
        loginInfo.setToken(token);
        loginInfoCache.put(token, loginInfo);
        log.info("用户登录成功:{}", token);
        return Resp.getInstance("000000", token);
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
