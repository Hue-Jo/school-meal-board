package com.zerobase.schoolmealboard.exceptions;

import com.zerobase.schoolmealboard.exceptions.custom.BannedUserException;
import com.zerobase.schoolmealboard.exceptions.custom.CommentNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.MealNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.ReviewNotFoundException;
import com.zerobase.schoolmealboard.exceptions.custom.UnAuthorizedUser;
import com.zerobase.schoolmealboard.exceptions.custom.UnReportableException;
import com.zerobase.schoolmealboard.exceptions.custom.UserNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage
        ));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }


  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleInvalidDateFormat(HttpMessageNotReadableException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(MealNotFoundException.class)
  public ResponseEntity<String> handleMealNotFoundException(MealNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ReviewNotFoundException.class)
  public ResponseEntity<String> handleReviewNotFoundException(ReviewNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(UnAuthorizedUser.class)
  public ResponseEntity<String> handleUnAuthorizedUser(UnAuthorizedUser ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(UnReportableException.class)
  public ResponseEntity<String> handleUnReportableException(UnReportableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(BannedUserException.class)
  public ResponseEntity<String> handleBannedUserException(BannedUserException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }
}
