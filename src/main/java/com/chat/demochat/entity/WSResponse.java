package com.chat.demochat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WSResponse
{
    private String mapped;

    private Object data;

    public static WSResponse wrap(String mapped, Object data)
    {
        return new WSResponse(mapped, data);
    }

}
