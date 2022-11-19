package com.chat.demochat.service.impl;

import com.chat.demochat.dao.NotifyRepository;
import com.chat.demochat.entity.Notify;
import com.chat.demochat.service.NotifyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NotifyServiceImpl implements NotifyService
{
    @Resource
    private NotifyRepository notifyRepository;

    @Override
    public void addNotify(Notify notify)
    {
    }

    @Override
    public void handleNotify(String id)
    {

    }
}
