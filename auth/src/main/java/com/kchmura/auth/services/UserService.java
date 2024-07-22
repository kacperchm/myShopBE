package com.kchmura.auth.services;

import com.kchmura.auth.entity.*;
import com.kchmura.auth.exceptions.UserExistingWithEmail;
import com.kchmura.auth.exceptions.UserExistingWithName;
import com.kchmura.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CookieService cookiService;
    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;


    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    private String generateToken(String username, int exp) {
        return jwtService.generateToken(username, exp);
    }

    public void validateToken(HttpServletRequest request, HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException {
        String token = null;
        String refresh = null;
        if (request.getCookies() != null) {
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals("token")) {
                    token = value.getValue();
                } else if (value.getName().equals("refresh")) {
                    refresh = value.getValue();
                }
            }
        } else {
            throw new IllegalArgumentException("Token can't be null");
        }
        try {
            jwtService.validateToken(token);
        } catch (IllegalArgumentException | ExpiredJwtException e) {
            jwtService.validateToken(refresh);
            Cookie refreshCookie = cookiService.generateCookie("refresh", jwtService.refreshToken(refresh, refreshExp), refreshExp);
            Cookie cookie = cookiService.generateCookie("Authorization", jwtService.refreshToken(refresh, exp), exp);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        }
    }


    public void register(UserRegisterDTO userDTO) throws UserExistingWithName, UserExistingWithEmail {

        userRepository.findUserByLogin(userDTO.getLogin()).ifPresent(value -> {
            throw new UserExistingWithName("Użytkownik o podanej nazwie już istnieje");
        });

        userRepository.findUserByEmail(userDTO.getEmail()).ifPresent(value -> {
            throw new UserExistingWithEmail("Użytkownik o podanym mailu już istnieje");
        });

        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        } else {
            user.setRole(Role.USER);
        }

        saveUser(user);
    }

    public ResponseEntity<?> login(HttpServletResponse response, User authRequest) {
        User user = userRepository.findUserByLogin(authRequest.getUsername()).orElse(null);
        if (user != null) {
            try {
                Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
                if (authenticate.isAuthenticated()) {
                    Cookie refresh = cookiService.generateCookie("refresh", generateToken(authRequest.getUsername(), refreshExp), refreshExp);
                    Cookie cookie = cookiService.generateCookie("token", generateToken(authRequest.getUsername(), exp), exp);
                    response.addCookie(cookie);
                    response.addCookie(refresh);
                    return ResponseEntity.ok(
                            UserRegisterDTO
                                    .builder()
                                    .login(user.getUsername())
                                    .email(user.getEmail())
                                    .role(user.getRole())
                                    .build());
                } else {
                    return ResponseEntity.ok(new AuthResponse(Code.A1));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

            }
        }
        return ResponseEntity.ok(new AuthResponse(Code.A2));
    }
}
