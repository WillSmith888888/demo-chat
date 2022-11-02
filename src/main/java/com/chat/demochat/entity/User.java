package com.chat.demochat.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable
{
    private String account;

    private String name;

}
