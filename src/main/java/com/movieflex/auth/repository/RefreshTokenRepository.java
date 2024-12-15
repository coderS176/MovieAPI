package com.movieflex.auth.repository;

import com.movieflex.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {
}
