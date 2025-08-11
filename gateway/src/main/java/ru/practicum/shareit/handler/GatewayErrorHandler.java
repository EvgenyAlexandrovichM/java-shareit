package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.errorresponse.ErrorResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GatewayErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException occurred", e);
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        errors.add("Validation exception: " + e.getMessage());
        return createErrorResponse(errors);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(List<String> errors) {
        if (errors != null && !errors.isEmpty()) {
            ErrorResponse response;
            if (errors.size() == 1) {
                response = new ErrorResponse(errors.getFirst(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());
            } else {
                response = new ErrorResponse(errors, LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            ErrorResponse response = new ErrorResponse("Unknown error", LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
