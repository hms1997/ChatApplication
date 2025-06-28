package com.example.ChatApp.user.repository;

import com.example.ChatApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // For login
    Optional<User> findByMobileNumber(String mobileNumber);

    // For contact sync
    List<User> findByMobileNumberIn(List<String> mobileNumbers);
}

