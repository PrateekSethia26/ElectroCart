package com.electrocart.inventory_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleInventoryNotFound(
            RuntimeException ex){
        if(ex.getMessage() !=null && ex.getMessage().startsWith(
                "Inventory not found for product ID"
        )){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status",404,
                            "error","Not Found",
                            "message",ex.getMessage()
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp",LocalDateTime.now(),
                        "status",500,
                        "error","Internal Server Error",
                        "message",ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleIllegalArgument(
            IllegalArgumentException ex
    ){
        return Map.of(
                "timestamp",LocalDateTime.now(),
                "status",400,
                "error","Bad Request",
                "message",ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleValidationErrors(
            MethodArgumentNotValidException ex
    ){
        Map<String,String > errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage())
                );
        return Map.of(
                "timestamp",LocalDateTime.now(),
                "status",400,
                "error","Bad Request",
                "message", "Validation failed",
                "errors", errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidRequestBody(
            HttpMessageNotReadableException ex
    ) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid or missing request body"
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(
                ex.getStatusCode().value()
        );

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", ex.getStatusCode().value(),
                        "error", status.getReasonPhrase(),
                        "message", ex.getReason() != null
                                ? ex.getReason()
                                : status.getReasonPhrase()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalState(
            IllegalStateException ex)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp",LocalDateTime.now(),
                        "status",409,
                        "error","Conflict",
                        "message",ex.getMessage()
                ));
    }
}
