package com.chat.demochat.entity;

import lombok.Data;

@Data
public class AddFriendReq
{
    private String account;

    private String friendAccount;
}
