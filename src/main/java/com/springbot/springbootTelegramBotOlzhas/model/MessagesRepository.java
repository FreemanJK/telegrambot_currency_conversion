package com.springbot.springbootTelegramBotOlzhas.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends CrudRepository<Messages, Long> {
    List<Messages> findByUser(User user);

}

