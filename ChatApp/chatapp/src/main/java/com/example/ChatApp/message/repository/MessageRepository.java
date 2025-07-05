package com.example.ChatApp.message.repository;

import com.example.ChatApp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page; // ✅ Import Page
import org.springframework.data.domain.Pageable; // ✅ Import Pageable

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

    // The query now sorts by timestamp DESC to get the latest messages first.
    // The method now accepts a Pageable object and returns a Page<Message>.
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
            "   OR (m.sender.id = :user2 AND m.receiver.id = :user1) " +
            "ORDER BY m.timestamp DESC") // <-- Changed to DESC
    Page<Message> findMessagesBetweenUsers(@Param("user1") String user1Id,
                                           @Param("user2") String user2Id,
                                           Pageable pageable); // <-- Added Pageable

    // ✅ ADD THIS NEW METHOD
    // Find all messages for a user with a specific status
    List<Message> findByReceiverIdAndStatusName(String receiverId, String statusName);

}
