package com.chatin.microbloggingappspringboot.dto;

import lombok.Data;

@Data
public class LoginRespDto {
    private String usernameOrEmail;
    private String token;

    public LoginRespDto(String usernameOrEmail, String token) {
        this.usernameOrEmail = usernameOrEmail;
        this.token = token;
    }

}
