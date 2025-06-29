package com.example.ChatApp.message.repository;

import com.example.ChatApp.entities.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    Optional<MessageStatus> findByName(String name);
}