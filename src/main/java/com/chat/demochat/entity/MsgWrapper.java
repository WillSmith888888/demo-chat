package com.chat.demochat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MsgWrapper
{
    private int type;

    private Object data;

    public static MsgWrapper wrap(int type, Object data)
    {
        return new MsgWrapper(type, data);
    }
}
