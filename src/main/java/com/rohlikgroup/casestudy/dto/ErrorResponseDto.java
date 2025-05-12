package com.rohlikgroup.casestudy.dto;

import org.springframework.http.HttpStatusCode;

import lombok.Data;

@Data
public class ErrorResponseDto {

    private String message;
    private String errorCode;
    private HttpStatusCode status;

    public ErrorResponseDto(HttpStatusCode status, String errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }
}
