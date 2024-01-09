package com.chatin.microbloggingappspringboot.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResDto {
    private HttpStatus httpStatus;
    private String message;

    public ErrorResDto(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
