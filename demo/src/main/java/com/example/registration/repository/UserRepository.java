package com.example.registration.repository;

import com.example.registration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE lower(u.email) = lower(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u.id FROM User u WHERE u.email = ?1")
    Optional<Long> findUserIdByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

}
