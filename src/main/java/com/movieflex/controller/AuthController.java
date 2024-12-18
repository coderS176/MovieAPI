package com.movieflex.controller;

import com.movieflex.auth.utils.AuthResponse;
import com.movieflex.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest registerRequest){
        return

    }
}
