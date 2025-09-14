package com.softdev.softdev.dto;

import lombok.Data;

@Data
public class APIResponseDTO<T> {
    private String message;
    private T data;
}
