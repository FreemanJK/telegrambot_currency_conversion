package com.springbot.springbootTelegramBotOlzhas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "users")
public class User {

    @Id
    private Long chatId;
    private String username;
    private LocalDateTime registeredAt;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @OneToMany(mappedBy = "user")
    private List<Messages> messages;

    public List<Messages> getMessages() {
        return messages;
    }

    public void setMessages(List<Messages> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", username='" + username + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
