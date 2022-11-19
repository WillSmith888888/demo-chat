package com.chat.demochat.exception;

import lombok.Data;

@Data
public class AlreadyFriendException extends Exception
{
    private String code = "000007";
    private String msg;

    public AlreadyFriendException(String account)
    {
        super("用户[" + account + "]已经是你的朋友");
        this.msg = "用户[" + account + "]已经是你的朋友";
    }
}
