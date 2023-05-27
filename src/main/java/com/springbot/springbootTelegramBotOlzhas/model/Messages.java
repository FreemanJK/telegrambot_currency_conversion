package com.springbot.springbootTelegramBotOlzhas.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column
    private String messageText;

    @Column
    private String username;

    @Column
    private LocalDateTime sentAt;
    @ManyToOne
    @JoinColumn(name = "users_chat_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
