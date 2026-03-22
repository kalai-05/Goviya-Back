package com.goviya;

import com.goviya.dto.ApiResponse;
import com.goviya.exceptions.GoviyaException;
import com.goviya.exceptions.ResourceNotFoundException;
import com.goviya.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // HTTP 404
    public ApiResponse<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // HTTP 403
    public ApiResponse<Object> handleUnauthorizedException(UnauthorizedException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ExceptionHandler(GoviyaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP 400
    public ApiResponse<Object> handleGoviyaException(GoviyaException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP 400
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String combinedErrors = String.join(", ", errors);
        return ApiResponse.error("Validation failed: " + combinedErrors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
    public ApiResponse<Object> handleAllExceptions(Exception ex) {
        log.error("Internal server error: ", ex);
        return ApiResponse.error("Internal server error");
    }
}
