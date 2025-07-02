package com.example.ChatApp.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "conversations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;
}

