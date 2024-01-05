//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.exception;

import com.threeatom.common.controller.Message;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DefaultExceptionHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public DefaultExceptionHandler() {
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public Message handlerException(Exception e) {
        return (new Message()).commonError(505, "Sorry, something is wrong!",e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({PermitException.class})
    public Message permitException(Exception e) {
        return (new Message()).commonError(510, "No permission for this!",e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({SystemException.class})
    public Message handlerSystemException(SystemException e) {
        if (e.getException() != null) {
            LOGGER.error("子集错误：", e.getException());
        }
        return (new Message()).commonError(e.getCode(), e.getMessage(),e);
    }

    @ResponseBody
    @ExceptionHandler({AuthorizationException.class})
    public Message handlerAuthorizationException(AuthorizationException e) {
        LOGGER.error("身份错误异常：", e);
        return e instanceof UnauthorizedException ? (new Message()).error(401, "您没有权限访问该内容") : (new Message()).error(607, "需要实名认证");
    }
}
