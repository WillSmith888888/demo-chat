package com.chat.demochat.exception;

import lombok.Data;

@Data
public class NotExistAccountException extends Exception
{
    private String code = "000006";
    private String msg;

    public NotExistAccountException(String account)
    {
        super("用户[" + account + "]不存在");
        this.msg = "用户[" + account + "]不存在";
    }
}
