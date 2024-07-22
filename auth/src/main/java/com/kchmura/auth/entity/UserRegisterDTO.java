package com.kchmura.auth.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Builder
public class UserRegisterDTO {
    @Length(min = 5, max = 50, message = "Login powinien mieć od 5 do 50 znaków")
    public String login;
    @Email
    public String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Length(min = 8, max = 75, message = "Hasło powinno składać się od 5 do 50 znaków")
    public String password;
    public Role role;
}