package com.chat.demochat.service;

import com.chat.demochat.entity.User;
import com.chat.demochat.exception.LoginException;
import com.chat.demochat.exception.Resp;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserService
{
    String createSession(List<String> accounts);

    void createUser(@RequestBody User user);

    User get(String account);

    void delByAccount(String account);

    Resp login(String account, String password) throws LoginException;

}
