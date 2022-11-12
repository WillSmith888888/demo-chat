package com.chat.demochat.entity;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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

    @ManyToMany
    @JoinTable(name = "t_friend",
            joinColumns = {@JoinColumn(name = "account", referencedColumnName = "account")},
            inverseJoinColumns = {@JoinColumn(name = "friend", referencedColumnName = "account")})
    private List<User> friends;

}
