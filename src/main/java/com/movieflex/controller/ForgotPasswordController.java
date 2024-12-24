package com.movieflex.controller;


import com.movieflex.auth.entity.ForgotPassword;
import com.movieflex.auth.entity.User;
import com.movieflex.auth.repository.ForgotPasswordRepository;
import com.movieflex.auth.repository.UserRepository;
import com.movieflex.auth.utils.ChangePassword;
import com.movieflex.dto.MailBody;
import com.movieflex.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // send mail for verification
    @PostMapping("/verifyEmail/{userName}")
    public ResponseEntity<String> verifyEmail(@PathVariable String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(()->new UsernameNotFoundException("Invalid Username"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .text("This is OTP for Your Fogot Password Request:" + otp)
                .Subject("OTP for forgot password request")
                .build();
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationDate(new Date(System.currentTimeMillis()*70*1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent successfully for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{userName}")
    public ResponseEntity<?> verifyOtp(@PathVariable Integer otp, @PathVariable String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(()->new UsernameNotFoundException("Invalid Username"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow(()-> new RuntimeException("Invalid OTP for userName:" + userName));

        if(fp.getExpirationDate().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFid());
            return new ResponseEntity<>("Otp has expired!",HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("Otp verified");
    }

    @PostMapping("/changePassword/{userName}")

    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String userName){

        if(!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
            return new ResponseEntity<>("Password didn't matched with repeat password!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(userName,encodedPassword);

        return ResponseEntity.ok("password changed successfully!");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

}
