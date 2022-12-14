package com.chat.demochat.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Slf4j
@Data
@Entity
@Table(name = "t_user")
@Proxy(lazy = false)
public class User implements Serializable
{
    @Id
    @NotFound(action = NotFoundAction.IGNORE)
    private String account;

    private String name;

    private String password;

    private String logo;

    @JsonIgnoreProperties({"friends", "password"})
    @ManyToMany
    @JoinTable(name = "t_friend",
            joinColumns = {@JoinColumn(name = "account", referencedColumnName = "account")},
            inverseJoinColumns = {@JoinColumn(name = "friend", referencedColumnName = "account")})
    private List<User> friends;

    @Override
    public String toString()
    {
        return account + " ---> " + name;
    }
}
