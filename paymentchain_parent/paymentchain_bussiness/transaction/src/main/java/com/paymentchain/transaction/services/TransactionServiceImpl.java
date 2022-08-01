package com.paymentchain.transaction.services;

import com.paymentchain.transaction.mappers.TransactionMapper;
import com.paymentchain.transaction.model.TransactionTPDTO;
import com.paymentchain.transaction.repositories.TransactionRepo;
import com.paymentchain.transaction.rest.TransactionApiDelegate;
import com.paymentchain_core.entities.Customer;
import com.paymentchain_core.entity_transaction.Transaction;
import com.paymentchain_core.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Objects;

@Transactional(noRollbackFor = {ResponseStatusException.class, HttpClientErrorException.class})
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionApiDelegate {

    private final RestTemplate restTemplate;
    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;

    @Override
    public ResponseEntity<Object> operationTP(TransactionTPDTO transactionTPDTO)
            throws ResponseStatusException {
        Transaction transaction = transactionMapper.transactionTpDtoToModel(transactionTPDTO);
        transaction.setTransactionDate(LocalDate.now());
        try {
            ResponseEntity<Customer> customer = restTemplate.exchange("http://microservicio-customer/customer-transaction/realize-operation?iban="
                    + transaction.getIban() + "&destination="
                    + transaction.getDestination() + "&paymentoperation="
                    + transaction.getPaymentOperation() + "&amount="
                    + transaction.getAmount(), HttpMethod.POST, HttpEntity.EMPTY, Customer.class);
            if (customer.getStatusCode().is2xxSuccessful())
                transaction.setAccountAmount(Objects.requireNonNull(customer.getBody()).getAccountBalance());
            log.info("Transaccion Liquidada");
            transaction.setStatus(TransactionStatus.LIQUIDADA);
            transaction.setAccountAmount(customer.getBody().getAccountBalance());
            transactionRepo.save(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionTPDTO);
            } catch (HttpServerErrorException exception) {
            log.error(exception.getMessage());
            transaction.setStatus(TransactionStatus.RECHAZADA);
            transaction.setAccountAmount(0.0);
            transactionRepo.save(transaction);
            return ResponseEntity.badRequest().body(exception.getResponseBodyAsString()
                    .replaceAll("\\{\\}", "").split(",")[3]);
            } catch (Exception exception){
            log.error(exception.getMessage());
            transaction.setStatus(TransactionStatus.RECHAZADA);
            transaction.setAccountAmount(0.0);
            transactionRepo.save(transaction);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR EN EL SERVER CUSTOMER: ", exception);
        }
    }
}