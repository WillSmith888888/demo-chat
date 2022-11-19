package com.chat.demochat.controller;

import com.chat.demochat.entity.Notify;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotifyController
{

    @PostMapping(value = "/addNotify.do")
    public Object addNotify(Notify notify)
    {

        return null;
    }

    @PostMapping(value = "/handleNotify.do")
    public Object handleNotify(String id)
    {

        return null;
    }
}
