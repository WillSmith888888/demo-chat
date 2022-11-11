package com.chat.demochat.entity;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "t_user")
@Data
@Proxy(lazy = false)
public class User implements Serializable
{
    @Id
    private String account;

    private String name;

    private String password;

}
