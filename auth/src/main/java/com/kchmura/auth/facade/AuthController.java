package com.kchmura.auth.facade;

import com.kchmura.auth.entity.AuthResponse;
import com.kchmura.auth.entity.Code;
import com.kchmura.auth.entity.User;
import com.kchmura.auth.entity.UserRegisterDTO;
import com.kchmura.auth.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    public final UserService userService;

    @PostMapping( "/register")
    public ResponseEntity<AuthResponse> addNewUser(@RequestBody UserRegisterDTO user) {
        userService.register(user);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        return userService.login(response, user);
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request) {
        try{
            userService.validateToken(request);
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT));
        }catch (IllegalArgumentException | ExpiredJwtException e){
            return ResponseEntity.status(401).body(new AuthResponse(Code.A3));
        }
    }

}
