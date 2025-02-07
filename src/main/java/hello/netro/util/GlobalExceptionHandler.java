package hello.netro.util;

import hello.netro.dto.ErrorResult;
import hello.netro.exception.TokenRefreshException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // Handle NoSuchElementException
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(NoSuchElementException.class)
//    public ErrorResult handleNoSuchElementException(NoSuchElementException e) {
//        log.error("Entity not found: {}", e.getMessage());
//        return new ErrorResult("NOT_FOUND", e.getMessage());
//    }
//
//    // Handle general exceptions
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public ErrorResult handleException(Exception e) {
//        log.error("Unexpected error: {}", e.getMessage());
//        return new ErrorResult("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");
//    }
//
//    @ExceptionHandler(TokenRefreshException.class)
//    public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
//    }
//}
