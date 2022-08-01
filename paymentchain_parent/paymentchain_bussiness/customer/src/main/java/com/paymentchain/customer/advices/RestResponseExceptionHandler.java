package com.paymentchain.customer.advices;

import com.paymentchain.customer.rest.CustomerApiController;
import com.paymentchain.customer.rest.TransactionApiController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;
@Slf4j
@ControllerAdvice(assignableTypes = {CustomerApiController.class, TransactionApiController.class})
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<?> handleClientError(HttpClientErrorException exception, WebRequest request){
        log.error("client error: " + exception.getMessage());
        return handleExceptionInternal(exception,
                exception.getResponseBodyAsString(),
                Optional.ofNullable(exception.getResponseHeaders()).orElse(new HttpHeaders()),
                exception.getStatusCode(),
                request) ;
    }
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<?> handleServerIllegalException(IllegalStateException exception, WebRequest request){
        log.error("illegal error: " + exception.getMessage());
        return handleExceptionInternal(
                exception,
                exception.getMessage(),
                new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE,
                request
        );
    }
    @ExceptionHandler(value = HttpServerErrorException.class)
    public ResponseEntity<?> handleHttpServerError(HttpServerErrorException exception, WebRequest request){
        return handleExceptionInternal(
               exception,
               exception.getResponseBodyAsString() + " cause: " + exception.getCause().getMessage(),
               Optional.ofNullable(exception.getResponseHeaders()).orElse(new HttpHeaders()),
                exception.getStatusCode(),
                request
        );
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> nonHandleExceptions(Exception exception, WebRequest request){
        log.error("nonHandle error: " + exception.getMessage());
        return handleExceptionInternal(
                exception,
                exception.getMessage(),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }
}
