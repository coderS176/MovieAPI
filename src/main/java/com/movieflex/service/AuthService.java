package com.movieflex.service;

import com.movieflex.auth.entity.User;
import com.movieflex.auth.utils.AuthResponse;
import com.movieflex.auth.utils.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public AuthResponse register(RegisterRequest registerRequest){
        var user = User.builder()
                .firstName()
    }

}
