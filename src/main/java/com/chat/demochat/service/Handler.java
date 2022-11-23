package com.chat.demochat.service;

public interface Handler
{
    void handle(String account, String type, String msg);
}
