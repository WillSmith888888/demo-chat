package com.chat.demochat.service;

import com.chat.demochat.entity.Notify;

public interface NotifyService
{
    void addNotify(Notify notify);

    void handleNotify(String id);
}
