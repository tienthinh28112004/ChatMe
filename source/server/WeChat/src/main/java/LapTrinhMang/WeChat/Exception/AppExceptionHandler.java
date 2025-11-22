package LapTrinhMang.WeChat.Exception;

import LapTrinhMang.WeChat.Dto.Response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

    /** Xử lý lỗi validation dữ liệu */
    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Lỗi dữ liệu đầu vào", errors);
    }

    /** Xử lý lỗi Bad Request (400) */
    @ExceptionHandler({
            BadRequestException.class,
            MultipartException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
            InvalidDataAccessApiUsageException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(final Exception e) {
        log.error("BadRequestException: {}", e.getMessage(), e);
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Xử lý lỗi Unauthorized (401) - Token hết hạn */
    @ExceptionHandler({
            TokenExpireException.class,
            RefreshTokenExpireException.class
    })
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(RuntimeException e) {
        log.error("TokenExpiredException: {}", e.getMessage(), e);
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /** Xử lý lỗi Unauthorized (401) - Sai thông tin đăng nhập */
    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final Exception e) {
        log.error("AuthenticationException: {}", e.getMessage(), e);
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /** Xử lý lỗi Forbidden (403) - Truy cập bị từ chối */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(final Exception e) {
        log.error("AccessDeniedException: {}", e.getMessage(), e);
        return build(HttpStatus.FORBIDDEN, e.getMessage());
    }

    /** Xử lý lỗi Not Found (404) */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage(), e);
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Xử lý tất cả các lỗi không xác định */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(final Exception e) {
        log.error("Unhandled Exception: {}", ExceptionUtils.getStackTrace(e));
        return build(HttpStatus.INTERNAL_SERVER_ERROR,"Hệ thông bị lỗi");
    }

    /** Hàm tạo response cho lỗi có danh sách lỗi */
    private ResponseEntity<ErrorResponse> build(HttpStatus httpStatus, String message, Map<String, String> errors) {
        return ResponseEntity.status(httpStatus).body(ErrorResponse.builder()
                .items(errors)
                .message(message)
                .build());
    }

    /** Hàm tạo response cho lỗi đơn giản */
    private ResponseEntity<ErrorResponse> build(final HttpStatus httpStatus, final String message) {
        return build(httpStatus, message, new HashMap<>());
    }
}