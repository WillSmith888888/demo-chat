package com.chat.demochat.entity;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "t_group_chat")
@Proxy(lazy = false)
public class GroupChat implements Serializable
{
    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "logo")
    private String logo;


}
