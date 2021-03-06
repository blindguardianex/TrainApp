package com.smartru.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity{

    @NotBlank
    @Size(min = 5, message = "Минимальная длинна логина: 5 символов")
    @Column(name = "login")
    private String login;

    @JsonIgnore
    @NotBlank
    @Size(min = 6, message = "Минимальная длинна пароля: 6 символов")
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "role")
    private String role;

    @JsonIgnore
    @Column(name = "access_token")
    private String accessToken;

    @JsonIgnore
    @Column(name = "refresh_token")
    private String refreshToken;

    @PrePersist
    void init(){
        role = "USER";
        super.init();
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                '}';
    }
}
