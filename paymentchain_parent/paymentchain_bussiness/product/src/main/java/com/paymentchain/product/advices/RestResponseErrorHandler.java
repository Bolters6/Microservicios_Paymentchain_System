package com.paymentchain.product.advices;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<?> resourceNotFoundHandler(ResourceNotFoundException resourceNotFoundException, WebRequest request){
        return handleExceptionInternal(resourceNotFoundException,
                "Producto No Existente",
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
                );
    }
}
