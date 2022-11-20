package com.chat.demochat.exception;

import lombok.Data;

@Data
public class AlreadyGroupException extends Exception
{
    private String code = "000008";
    private String msg;

    public AlreadyGroupException(String sessionId)
    {
        super("群[" + sessionId + "]已经存在");
        this.msg = "群[" + sessionId + "]已经存在";
    }
}
