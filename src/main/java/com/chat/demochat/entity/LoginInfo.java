package com.chat.demochat.entity;

import lombok.Data;

@Data
public class LoginInfo
{
    private User user;

    private String token;
}
