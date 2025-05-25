package com.example.registration.exception.handler;

import com.example.registration.exception.exception.EmailNotRegisteredException;
import com.example.registration.exception.exception.IncorrectPasswordException;
import com.example.registration.exception.exception.PasswordNotConfirmException;
import com.example.registration.exception.exception.RepeatedEmailException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // 高优先级处理
public class GlobalExceptionHandler {



    // 处理自定义业务异常
    @ExceptionHandler(EmailNotRegisteredException.class)
    public ResponseEntity<?> handler(EmailNotRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "errorType", "email",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<?> handler(IncorrectPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "errorType", "password",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(RepeatedEmailException.class)
    ResponseEntity<?> handler(RepeatedEmailException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "errorType", "email",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(PasswordNotConfirmException.class)
    ResponseEntity<?> handler(PasswordNotConfirmException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "errorType", "password",
                "message", ex.getMessage()
        ));
    }


}
