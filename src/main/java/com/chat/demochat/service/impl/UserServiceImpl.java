package com.chat.demochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.cons.Constant;
import com.chat.demochat.dao.UserRepository;
import com.chat.demochat.entity.LoginInfo;
import com.chat.demochat.entity.User;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.service.UserService;
import com.chat.demochat.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

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

    @Resource
    private CacheManager cacheManager;

    @Resource
    private UserRepository userRepository;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;


    @Override
    public void createUser(User user)
    {
//        Cache userCache = cacheManager.getCache("userCache");
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
//        userCache.put(user.getAccount(), user);
        userRepository.save(user);

    }

    @Override
    public User getByToken(String token) throws LoginException
    {
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            throw new LoginException("000003", "用户登录信息失效");
        }
        return get(loginInfo.getUser().getAccount());
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
    public String login(String account, String password) throws LoginException
    {
        User user = userRepository.getReferenceById(account);
        log.info("用户信息：{}", JSON.toJSONString(user));
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
        String token = String.valueOf(UUID.randomUUID());
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUser(user);
        loginInfo.setToken(token);
        cache.asMap().put(token, loginInfo);
        log.info("用户登录成功:{}", token);
        return token;
    }

    @Override
    public String getSessionId(String accounts) throws LoginException
    {
        List<String> accountList = Arrays.asList(accounts.split(","));
        String token = accountList.get(0);
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            throw new LoginException("000004", "登录信息失效");
        }
        accountList.set(0, loginInfo.getUser().getAccount());
        return Utils.getSessionId(accountList);
    }

    @Override
    public List<User> getFriends(String token) throws LoginException
    {
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            throw new LoginException("000004", "用户登录信息失效");
        }
        String account = loginInfo.getUser().getAccount();
        return null;
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


    @Override
    public void addFriend(String account, String friend)
    {

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
