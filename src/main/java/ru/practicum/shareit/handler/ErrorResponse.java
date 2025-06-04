package ru.practicum.shareit.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private List<String> errors;
    private LocalDateTime timestamp;
    private int status;
}
