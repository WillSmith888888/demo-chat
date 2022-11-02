package com.chat.demochat.entity;

import lombok.Data;

import java.util.List;

@Data
public class Session
{
    private String sessionId;

    private List<String> accounts;
}
