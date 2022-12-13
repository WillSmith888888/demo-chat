package com.chat.demochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.component.SessionPool;
import com.chat.demochat.cons.Constant;
import com.chat.demochat.dao.GroupChatRepository;
import com.chat.demochat.dao.UserRepository;
import com.chat.demochat.entity.*;
import com.chat.demochat.exception.AlreadyFriendException;
import com.chat.demochat.exception.AlreadyGroupException;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.NotExistAccountException;
import com.chat.demochat.service.UserService;
import com.chat.demochat.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

@Transactional
@Slf4j
@Service
public class UserServiceImpl implements UserService
{

    private static final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private Consumer<String, String> consumer;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private UserRepository userRepository;

    @Resource
    private GroupChatRepository groupChatRepository;

    @Resource
    private SessionPool sessionPool;

    @Resource(name = "loginInfoCache")
    private com.github.benmanes.caffeine.cache.Cache<String, LoginInfo> cache;

    @Resource
    private AdminClient adminClient;


    @Override
    public User createUser(User user)
    {
        // 1.先保存用户
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User get(String account)
    {
//        Cache userCache = cacheManager.getCache("userCache");
//        User user = userCache.get(account, User.class);
        User user = userRepository.getReferenceById(account);
        log.info(JSON.toJSONString(user));
        return user;
    }

    @Override
    public void delByAccount(String account)
    {
        User user = userRepository.getReferenceById(account);
        userRepository.delete(user);
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
    public void logout(String token) throws LoginException, IOException
    {
        LoginInfo loginInfo = cache.asMap().get(token);
        sessionPool.remove(loginInfo.getUser().getAccount());
        cache.asMap().remove(token);
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
        String sessionId = Utils.getSessionId(accountList);
        initSession(sessionId);
        return sessionId;
    }

    public void initSession(String sessionId)
    {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        try
        {
            boolean exist = listTopicsResult.names().get().contains(sessionId);
            if (!exist)
            {
//                MsgInfo msgInfo = new MsgInfo();
//                msgInfo.setSessionId(sessionId);
//                msgInfo.setAccount(account);
//                msgInfo.setTime(ExtDate.getCurrentTimeStr());
//                msgInfo.setContent("我开启了会话");
//                kafkaTemplate.send(sessionId, JSON.toJSONString(msgInfo));
                createTopic(sessionId);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public User addFriend(String account, String friendAccount) throws NotExistAccountException, AlreadyFriendException
    {
        User friend = userRepository.getReferenceById(friendAccount);
        if (friend == null)
        {
            throw new NotExistAccountException(friendAccount);
        }
        User user = userRepository.getReferenceById(account);
        for (User _friend : user.getFriends())
        {
            if (friend.getAccount().equals(_friend.getAccount()))
            {
                throw new AlreadyFriendException(account);
            }
        }
        saveFriend(user, friend);
        saveFriend(friend, user);
        return friend;
    }

    @Override
    public void removeFriend(String account, String friendAccount)
    {
        User friend = userRepository.getReferenceById(friendAccount);
        User user = userRepository.getReferenceById(account);
        List<User> friends = user.getFriends();
        log.info("用户[]朋友列表:{}", account, JSON.toJSONString(friends));
        friends.removeIf(_friend -> _friend.getAccount().equals(friend.getAccount()));
        userRepository.save(user);
        friends = friend.getFriends();
        log.info("用户[]朋友列表:{}", friendAccount, JSON.toJSONString(friends));
        friends.removeIf(_friend -> _friend.getAccount().equals(user.getAccount()));
        userRepository.save(friend);
    }

    private void saveFriend(User self, User friend)
    {
        List<User> friends = self.getFriends();
        if (friends == null)
        {
            friends = new ArrayList<>();
            self.setFriends(friends);
        }
        friends.add(friend);
        userRepository.save(self);
    }

    @Override
    public GroupChat createGroupChat(String groupName, String accounts, String logo) throws LoginException, AlreadyGroupException
    {
        List<String> accountList = Arrays.asList(accounts.split(","));
        String token = accountList.get(0);
        LoginInfo loginInfo = cache.asMap().get(token);
        if (loginInfo == null)
        {
            throw new LoginException("000004", "登录信息失效");
        }
        accountList.set(0, loginInfo.getUser().getAccount());
        String sessionId = Utils.getSessionId(accountList);
        if (groupChatRepository.existsById(sessionId))
        {
            throw new AlreadyGroupException(sessionId);
        }
        GroupChat groupChat = new GroupChat();
        groupChat.setGroupName(groupName);
        groupChat.setLogo(logo);
        groupChat.setSessionId(sessionId);

        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setAccount(accountList.get(0));
        msgInfo.setSessionId(sessionId);
        msgInfo.setContent("大家好，我创建了群[" + groupName + "]");
        msgInfo.setTime(dateformat.format(new Date()));
        kafkaTemplate.send(msgInfo.getSessionId(), JSON.toJSONString(msgInfo));
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat getGroupChat(String sessionId) throws LoginException
    {
        GroupChat groupChat = groupChatRepository.getReferenceById(sessionId);
        log.info("查询到的群信息：{}", JSON.toJSONString(groupChat));
        return groupChat;
    }

    @Override
    public void delGroupChat(String sessionId) throws LoginException
    {
        if (groupChatRepository.existsById(sessionId))
        {
            groupChatRepository.deleteById(sessionId);
        }
        deleteTopic(sessionId);
    }

    public void deleteTopic(String sessionId)
    {
        List<TopicPartition> tps = new ArrayList<>();
        TopicPartition tp = new TopicPartition(sessionId, 0);
        tps.add(tp);
        consumer.assign(tps);
        consumer.seekToEnd(tps);
        long position = consumer.position(tp);
        RecordsToDelete recordsToDelete = RecordsToDelete.beforeOffset(position);
        Map<TopicPartition, RecordsToDelete> map = new HashMap<>();
        map.put(tp, recordsToDelete);
        adminClient.deleteRecords(map);
    }

    public void createTopic(String sessionId)
    {
        List<NewTopic> topics = new ArrayList<>();
        NewTopic topic = new NewTopic(sessionId, 1, (short) 1);
        topics.add(topic);
        CreateTopicsResult result = adminClient.createTopics(topics);
        try
        {
            log.info("创建topic结果：{}", result.topicId(sessionId).get().toString());
        }
        catch (Exception e)
        {
            log.error("创建topic出现异常{}", e);
            e.printStackTrace();
        }
    }
}
