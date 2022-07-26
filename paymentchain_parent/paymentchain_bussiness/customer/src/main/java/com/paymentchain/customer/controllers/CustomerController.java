package com.paymentchain.customer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/cc")
public class CustomerController {
    @GetMapping(path = "echo")
    public ResponseEntity<String> echo(){
        return ResponseEntity.ok().body("OK");
    }
}
