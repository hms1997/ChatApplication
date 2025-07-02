package com.example.ChatApp.message.repository;

import com.example.ChatApp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
            "   OR (m.sender.id = :user2 AND m.receiver.id = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findMessagesBetweenUsers(@Param("user1") String user1Id,
                                           @Param("user2") String user2Id);

}
