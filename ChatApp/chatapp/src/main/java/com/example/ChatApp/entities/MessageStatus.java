package com.example.ChatApp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "message_statuses")
public class MessageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // SENT, DELIVERED, READ

    // Getters, setters, constructors


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

