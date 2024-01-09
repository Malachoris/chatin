package com.chatin.microbloggingappspringboot.dto;

import lombok.Data;

@Data
public class LoginRespDto {
    private String email;
    private String token;

    public LoginRespDto(String email, String token) {
        this.email = email;
        this.token = token;
    }

}
