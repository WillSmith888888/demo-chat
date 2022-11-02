package com.chat.demochat.entity;

import lombok.Data;

@Data
public class MsgInfo
{
    private String sessionId;

    private String content;

    private String time;

    private String account;
}
