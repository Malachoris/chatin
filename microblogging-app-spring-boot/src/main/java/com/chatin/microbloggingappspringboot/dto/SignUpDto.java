package com.chatin.microbloggingappspringboot.dto;

import lombok.Data;

@Data
public class SignUpDto {
    private String firstName;
    private String username;
    private String email;
    private String password;
}
