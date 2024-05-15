package com.kchmura.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegisterDTO {
    public String login;
    public String email;
    public String password;
    public Role role;
}

