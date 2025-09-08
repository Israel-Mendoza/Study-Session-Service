package dev.artisra.studysessionservice.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import dev.artisra.studysessionservice.models.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralException> handleValidationExceptions(@NotNull MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        BindingResult bindingResult = ex.getBindingResult();

        bindingResult.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        GeneralException generalException = new GeneralException(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                errors,
                request.getDescription(false)
        );

        return new ResponseEntity<>(generalException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyProcessedCommandException.class)
    public ResponseEntity<GeneralException> handleAlreadyProcessedCommandException(AlreadyProcessedCommandException ex, WebRequest request) {
        GeneralException generalException = new GeneralException(
                LocalDateTime.now(),
                HttpStatus.ALREADY_REPORTED.value(),
                Map.of("message", ex.getMessage()),
                request.getDescription(false)
        );

        return new ResponseEntity<>(generalException, HttpStatus.ALREADY_REPORTED);
    }
}
