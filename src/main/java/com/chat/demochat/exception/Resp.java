package com.chat.demochat.exception;

import lombok.Data;

@Data
public class Resp
{
    private String code;

    private String msg;

    private Object data;

    public static Resp getInstance(String code, String msg, Object data)
    {
        Resp resp = new Resp();
        resp.setCode(code);
        resp.setMsg(msg);
        resp.setData(data);
        return resp;
    }

    public static Resp getInstance(String code, String msg)
    {
        return getInstance(code, msg, null);
    }

    public static Resp getInstance(String code, Object data)
    {
        return getInstance(code, null, data);
    }
}
