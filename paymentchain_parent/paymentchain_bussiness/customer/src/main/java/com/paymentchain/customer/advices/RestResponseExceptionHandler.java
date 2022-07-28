package com.paymentchain.customer.advices;

import com.paymentchain.customer.rest.CustomerApiController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice(assignableTypes = {CustomerApiController.class})
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<?> handleClientError(HttpClientErrorException exception, WebRequest request){
        return handleExceptionInternal(exception,
                exception.getResponseBodyAsString(),
                Optional.ofNullable(exception.getResponseHeaders()).orElse(new HttpHeaders()),
                exception.getStatusCode(),
                request) ;
    }
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<?> handleServerIllegalException(IllegalStateException exception, WebRequest request){
        return handleExceptionInternal(
                exception,
                exception.getMessage(),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}
