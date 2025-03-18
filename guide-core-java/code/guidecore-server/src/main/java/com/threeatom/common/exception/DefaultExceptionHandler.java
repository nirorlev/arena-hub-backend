package com.threeatom.common.exception;

import com.threeatom.common.controller.Message;
import io.sentry.Sentry;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public DefaultExceptionHandler() {
    }

    private void sendExceptionToSentry(Exception e) {
        Sentry.captureException(e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Message handlerException(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            LOGGER.error(errorMessage, e);
        }
        sendExceptionToSentry(e);
        return new Message().commonError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sorry, something is wrong!", e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({PermitException.class})
    public Message permitException(Exception e) {
        return (new Message()).commonError(HttpStatus.FORBIDDEN.value(), "No permission for this!", e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ForbiddenException.class)
    public Message forbiddenException(Exception e) {
        return new Message().commonError(HttpStatus.FORBIDDEN.value(), e.getMessage(), e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({SystemException.class})
    public Message handlerSystemException(SystemException e) {
        if (e.getException() != null) {
            LOGGER.error("子集错误：", e.getException());
        }
        return (new Message()).commonError(e.getCode(), e.getMessage(), e);
    }

    @ResponseBody
    @ExceptionHandler({AuthorizationException.class})
    public Message handlerAuthorizationException(AuthorizationException e) {
        LOGGER.error("身份错误异常：", e);
        return e instanceof UnauthorizedException
            ? (new Message()).error(HttpStatus.UNAUTHORIZED.value(), "您没有权限访问该内容")
            : (new Message()).error(607, "需要实名认证");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Message handleResourceNotFoundException(ResourceNotFoundException e) {
        return new Message().commonError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Message handleValidationException(ValidationException e) {
        return new Message().commonError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e);
    }

    @ExceptionHandler(BindException.class)
    public Message handleBindException(BindException ex) {
        return handleValidationException(ex.getBindingResult());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(VideoPlaySegmentNotUpdatedException.class)
    public Message handlerVideoPlaySegmentNotUpdatedException(VideoPlaySegmentNotUpdatedException e) {
        return (new Message()).commonError(HttpStatus.CONFLICT.value(), e.getMessage(), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Message handleValidationExceptions(MethodArgumentNotValidException ex) {
        return handleValidationException(ex.getBindingResult());
    }

    private Message handleValidationException(BindingResult ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                String objectName = error.getObjectName();
                String errorMessage = error.getDefaultMessage();
                errors.put(objectName, errorMessage);
            }
        });

        return new Message().validationError("Validation error", errors);
    }
}
