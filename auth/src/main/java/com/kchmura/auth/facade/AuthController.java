package com.kchmura.auth.facade;

import com.kchmura.auth.entity.AuthResponse;
import com.kchmura.auth.entity.Code;
import com.kchmura.auth.entity.UserRegisterDTO;
import com.kchmura.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    public final UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> addNewUser(@RequestBody UserRegisterDTO user) {
        userService.register(user);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }
}
