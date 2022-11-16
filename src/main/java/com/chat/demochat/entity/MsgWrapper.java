package com.chat.demochat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Data
@AllArgsConstructor
public class MsgWrapper
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.addMixIn(User.class, MyMixIn.class);
    }

    private int type;

    private Object data;

    public static MsgWrapper wrap(int type, Object data)
    {
        return new MsgWrapper(type, data);
    }

    @SneakyThrows
    public String toString()
    {
        return MAPPER.writeValueAsString(this);
    }

    public abstract static class MyMixIn
    {
        @JsonIgnore
        private String password;
    }
}
