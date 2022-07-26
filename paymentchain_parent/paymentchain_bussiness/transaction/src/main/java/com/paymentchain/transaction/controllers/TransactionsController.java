package com.paymentchain.transaction.controllers;

import com.paymentchain.transaction.mappers.TransactionMapper;
import com.paymentchain.transaction.repositories.TransactionRepo;
import com.paymentchain_core.dto.TransactionDto;
import com.paymentchain_core.entities.Customer;
import com.paymentchain_core.entity_transaction.Transaction;
import com.paymentchain_core.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
@RequestMapping(path = "/transaction")
public class TransactionsController {

    private final TransactionRepo transactionRepo;
    private final RestTemplate restTemplate;
    private final TransactionMapper transactionMapper;

    @PostMapping(path = "/depositandretire", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> operationTransaction(@RequestBody @Valid TransactionDto transactionDto)
            throws IllegalArgumentException, HttpClientErrorException{
        Transaction transaction = transactionMapper.transactionDtoToModel(transactionDto);
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.LIQUIDADA);
        try {
            ResponseEntity<Customer> customer = restTemplate.exchange("http://microservicio-customer/customer-transaction/realize-operation?iban="
                    + transaction.getIban() + "&paymentoperation="
                    + transaction.getPaymentOperation()
                    + "&amount="
                    + transaction.getAmount(), HttpMethod.POST, HttpEntity.EMPTY, Customer.class);
           if(customer.getStatusCode().is2xxSuccessful()) transaction.setAccountAmount(Objects.requireNonNull(customer.getBody()).getAccountBalance());
           log.info("Transaccion Liquidada");
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepo.save(transaction));
        }catch (Exception exception){
            log.error("Transaccion Rechazada");
            log.error(exception.getMessage());
            return ResponseEntity.badRequest()
                    .body(transactionRepo.save(transaction
                                    .setStatus(TransactionStatus.RECHAZADA)
                                    .setAccountAmount(0.0)));
        }
    }
    @Transactional(readOnly = true)
    @GetMapping(path = "/gettransactions/{iban}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String iban) throws HttpClientErrorException {
        return ResponseEntity.ok().body(transactionRepo.findAllByIban(iban));
    }
    @Transactional(readOnly = true)
    @GetMapping(path = "/gettransactions/{iban}/date1/{date1}/date2/{date2}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String iban,
                                                       @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date1,
                                                       @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date2) throws HttpClientErrorException{
        try {
            return ResponseEntity.ok().body(transactionRepo
                    .findAllByIbanAndTransactionDateBetween(iban, date1, date2));
        }catch (Exception exception){
            log.error("Error en el cliente por no se que");
            return ResponseEntity.internalServerError().build();
        }
    }
}
