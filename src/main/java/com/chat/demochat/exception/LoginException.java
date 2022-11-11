package com.chat.demochat.exception;

import lombok.Data;

@Data
public class LoginException extends Exception
{
    private String code;
    private String msg;

    public LoginException(String code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }
}
