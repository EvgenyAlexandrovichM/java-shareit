package ru.practicum.shareit.handler;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponse {
    private List<String> errors;
    private String error;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponse(List<String> errors, LocalDateTime timestamp, int status) {
        this.errors = errors;
        this.error = null;
        this.timestamp = timestamp;
        this.status = status;
    }

    public ErrorResponse(String error, LocalDateTime timestamp, int status) {
        this.errors = null;
        this.error = error;
        this.timestamp = timestamp;
        this.status = status;
    }
}
