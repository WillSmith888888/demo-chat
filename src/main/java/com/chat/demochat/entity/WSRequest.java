package com.chat.demochat.entity;

import lombok.Data;

@Data
public class WSRequest
{
    private String token;

    private String mapped;

    private String body;
}
