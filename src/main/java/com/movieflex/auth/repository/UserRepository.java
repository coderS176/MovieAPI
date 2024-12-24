package com.movieflex.auth.repository;

import com.movieflex.auth.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer>{

    Optional<User> findByUsername(String username);


    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.username = ?1")
    void updatePassword(String userName, String encodedPassword);

}
