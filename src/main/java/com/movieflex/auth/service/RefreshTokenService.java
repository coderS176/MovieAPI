package com.movieflex.auth.service;

import com.movieflex.auth.entity.RefreshToken;
import com.movieflex.auth.entity.User;
import com.movieflex.auth.repository.RefreshTokenRepository;
import com.movieflex.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String userName){
       User user = userRepository.findByUsername(userName)
               .orElseThrow(()-> new UsernameNotFoundException("User Not Found With userName:"+userName));

       RefreshToken refreshToken = user.getRefreshToken();

       if(refreshToken == null){
           long refreshTokenValidity = 5*60*100000;
           refreshToken = RefreshToken.builder()
                   .refreshToken(UUID.randomUUID().toString())
                   .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                   .user(user)
                   .build();
           refreshTokenRepository.save(refreshToken);
       }
      return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
       RefreshToken refToken =  refreshTokenRepository.findByRefreshToken(refreshToken)
               .orElseThrow(()->new RuntimeException("Token Not Found"));
       if(refToken.getExpirationTime().compareTo(Instant.now())<0){
           refreshTokenRepository.delete(refToken);
           throw new RuntimeException("Refresh Token Expired");
       }
       return refToken;
    }
}
