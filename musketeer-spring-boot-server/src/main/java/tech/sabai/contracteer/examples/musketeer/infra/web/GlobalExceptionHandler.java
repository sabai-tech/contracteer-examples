package tech.sabai.contracteer.examples.musketeer.infra.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationError(MethodArgumentNotValidException ex) {
    var detail = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> "'" + e.getField() + "' " + e.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");

    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problem.setTitle("Validation failed");
    return ResponseEntity.badRequest().contentType(PROBLEM_JSON).body(problem);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    var problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "'" + ex.getName() + "' must be a valid value");
    problem.setTitle("Invalid parameter");
    return ResponseEntity.badRequest().contentType(PROBLEM_JSON).body(problem);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleUnreadableMessage(HttpMessageNotReadableException ex) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed request body");
    problem.setTitle("Invalid request");
    return ResponseEntity.badRequest().contentType(PROBLEM_JSON).body(problem);
  }
}
