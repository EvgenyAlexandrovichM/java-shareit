package ru.practicum.shareit.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.errorresponse.ErrorResponse;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ServerErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException occurred", e);
        List<String> errors = new ArrayList<>();
        errors.add("Not found: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        log.error("ForbiddenException occurred", e);
        List<String> errors = new ArrayList<>();
        errors.add("Access denied: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        log.error("ConflictException occurred", e);
        List<String> errors = new ArrayList<>();
        errors.add("Conflict: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.error("BadRequestException occurred", e);
        List<String> errors = new ArrayList<>();
        errors.add("Bad request: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        log.error("Unhandled exception occurred", e);
        List<String> errors = new ArrayList<>();
        errors.add("Server error: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("ConstraintViolationException occurred", ex);

        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        return createErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(List<String> errors, HttpStatus status) {
        if (errors != null && !errors.isEmpty()) {
            ErrorResponse response;
            if (errors.size() == 1) {
                response = new ErrorResponse(errors.getFirst(), LocalDateTime.now(), status.value());
            } else {
                response = new ErrorResponse(errors, LocalDateTime.now(), status.value());
            }
            return new ResponseEntity<>(response, status);
        } else {
            ErrorResponse response = new ErrorResponse("Unknown error", LocalDateTime.now(), status.value());
            return new ResponseEntity<>(response, status);
        }
    }
}
