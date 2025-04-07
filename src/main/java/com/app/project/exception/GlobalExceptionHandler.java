package com.app.project.exception;

import com.app.project.common.BaseResponse;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author 
 * @from 
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    /**
     * 处理 `@Valid` 校验失败异常（针对 `@RequestBody` 参数）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<String> errorMessages = result.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "参数错误")
                .collect(Collectors.toList());

        log.error("参数校验失败: {}", errorMessages);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, String.join(", ", errorMessages));
    }

    /**
     * 处理 `@RequestParam` 和 `@PathVariable` 校验失败的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errorMessages = violations.stream()
                .map(violation -> violation.getMessage() != null ? violation.getMessage() : "参数错误")
                .collect(Collectors.toList());

        log.error("参数校验失败: {}", errorMessages);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, String.join(", ", errorMessages));
    }

    /**
     * 处理缺少请求参数的异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数: {}", e.getParameterName());
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, e.getParameterName() + " 参数缺失");
    }
}
