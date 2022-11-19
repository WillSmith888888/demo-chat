package com.chat.demochat.entity;

import lombok.Data;

import javax.persistence.*;


@Data
@Table(name = "t_notify")
@Entity
public class Notify
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    private int type;

    @Column(name = "msg")
    private String msg;
}
