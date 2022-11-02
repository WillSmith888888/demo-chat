package com.chat.demochat.service;

import com.chat.demochat.entity.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserService
{
    String createSession(List<String> accounts);

    void createUser(@RequestBody User user);

    User get(String account);
}
