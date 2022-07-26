package com.paymentchain.customer.controllers;

import com.paymentchain.customer.repositories.CustomerRepo;
import com.paymentchain_core.dto.TransactionDto;
import com.paymentchain_core.entities.Customer;
import com.paymentchain_core.entity_transaction.Transaction;
import com.paymentchain_core.enums.PaymentOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "customer-transaction")
@Transactional
public class CustomerTransactions {

    private final CustomerRepo customerRepo;
    private final RestTemplate restTemplate;

    @PostMapping(path = "/depositandretire", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> operationTransaction(@RequestBody @Valid TransactionDto transactionDto){

        Optional<Customer> customer = customerRepo.findByIban(transactionDto.getIban());
        if(customer.isEmpty()){
            log.error("Customer no Existe");
            return ResponseEntity.notFound().build();
        }
        try {

            ResponseEntity<Transaction> response = restTemplate.postForEntity("http://microservicio-transaction/transaction/depositandretire", transactionDto, Transaction.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Transaccion Realizada");
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Transaccion realizada correctamente: " + response.getBody());
            }
            return ResponseEntity.badRequest().body("Problemas con las Transacciones quien sabe porque");

        }catch (HttpClientErrorException exception){

            log.error("Transaccion Rechazada");
            return ResponseEntity
                    .status(exception.getStatusCode())
                    .body("Transaccion Rechazada" + exception.getMessage());

        }catch (Exception exception){

            log.error("ERROR EN EL SERVIDOR: {}", exception.getMessage());
            log.info("CUSTOMER {}" , customer.get());
            customerRepo.save(customer.get());
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @PostMapping(path = "/realize-operation", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> realizeOperation(@RequestParam(name = "iban") String iban,
                                              @RequestParam(name = "paymentoperation") PaymentOperation paymentOperation,
                                              @RequestParam(name = "amount") Double amount) throws IllegalArgumentException {
        switch (paymentOperation){
            case DEPOSITAR -> {
                return customerRepo.findByIban(iban)
                        .map(customer -> ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(customer.setAccountBalance(customer.getAccountBalance() + amount - 1)))
                        .orElseThrow(() -> new IllegalArgumentException("Iban Erroneo"));
            }
            case RETIRAR -> {
                return customerRepo.findByIban(iban)
                        .map(customer -> ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(customer.setAccountBalance(customer.getAccountBalance() - amount - 2.5)))
                        .filter(customerResponseEntity -> {
                            if(customerResponseEntity.getBody().getAccountBalance() > 0){
                                return true;
                            }
                            customerResponseEntity.getBody()
                                    .setAccountBalance(customerResponseEntity.getBody().getAccountBalance() + amount);
                            return false;
                        })
                        .orElseThrow(() -> new IllegalArgumentException("Fondo insuficiente"));
            }
            default -> {
                throw new IllegalArgumentException("Operacion no Soportada");
            }
        }
    }
    @Transactional(readOnly = true)
    @GetMapping(path = "/gettransactions/{iban}")
    public ResponseEntity<?> getTransactions(@PathVariable String iban){
        Map<String, String> uriVariables = new HashMap<>(Map.of("iban", iban));
        try {
            ResponseEntity<Transaction[]> response = restTemplate.getForEntity("http://microservicio-transaction/transaction/gettransactions/{iban}", Transaction[].class, uriVariables);
            if(response.getStatusCode().is2xxSuccessful()){
                log.info("request completa");
                return ResponseEntity.ok().body(response.getBody());
            }
            return ResponseEntity.badRequest().body("Solicitud no completada");
        }catch (HttpClientErrorException exception){
            log.error("Error en el cliente");
            return ResponseEntity.internalServerError().body("Error en el cliente: " + exception.getMessage());
        }catch (Exception exception){
            log.error("ERROR EN EL SERVER");
            return ResponseEntity.internalServerError().body("ERROR EN EL SERVER: " + exception.getMessage());
        }
    }
    @Transactional(readOnly = true)
    @GetMapping(path = "/gettransactions/{iban}/date1/{date1}/date2/{date2}")
    public ResponseEntity<?> getTransactions(@PathVariable String iban,
                                             @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date1,
                                             @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date2){

        Map<String, String> uriVariables = new HashMap<>(Map.of("iban", iban,
                "date1", date1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                "date2", date2.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        try {
            ResponseEntity<Transaction[]> response = restTemplate.getForEntity("http://microservicio-transaction/transaction/gettransactions/{iban}/date1/{date1}/date2/{date2}", Transaction[].class, uriVariables);
            if(response.getStatusCode().is2xxSuccessful()){
                log.info("request completa");
                return ResponseEntity.ok().body(response.getBody());
            }
            return ResponseEntity.badRequest().body("Solicitud no completada");
        }catch (HttpClientErrorException exception){
            log.error("Error en el cliente");
            return ResponseEntity.internalServerError().body("Error en el cliente: " + exception.getMessage());
        }catch (Exception exception){
            log.error("Error en el server: " +  exception.getMessage());
            return ResponseEntity.internalServerError().body("ERROR EN EL SERVER: " + exception.getMessage());
        }
    }
}
