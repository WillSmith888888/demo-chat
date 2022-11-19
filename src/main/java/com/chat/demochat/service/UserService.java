package com.chat.demochat.service;

import com.chat.demochat.entity.User;
import com.chat.demochat.exception.AlreadyFriendException;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.NotExistAccountException;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface UserService
{
    void createUser(@RequestBody User user);

    User get(String account);

    void delByAccount(String account);

    String login(String account, String password) throws LoginException;

    void logout(String token) throws LoginException, IOException;

    String getSessionId(String accounts) throws LoginException;

    User addFriend(String account, String friendAccount) throws NotExistAccountException, AlreadyFriendException;

    void removeFriend(String account, String friendAccount);

}
