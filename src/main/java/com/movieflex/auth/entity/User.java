package com.movieflex.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "First Name can't be blank")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Username can't be blank")
    @Column(unique = true)
    private String username;

    @NotNull(message = "Password can't be null")
    @Size(min=5, message = "password must have at least 5 letters")
    private String password;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @NotBlank(message = "Email can't be blank")
    @Column(unique = true)
    @Email(message = "Please Enter Email in proper format")
    private String email;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public RefreshToken getRefreshToken() throws NullPointerException {
        return this.refreshToken;
    }
}
