package com.paymentchain.customer.services;

import com.paymentchain.customer.model.TransactionTPDTO;
import com.paymentchain.customer.repositories.CustomerRepo;
import com.paymentchain.customer.rest.TransactionApiDelegate;
import com.paymentchain_core.entities.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionApiDelegate {

    private final CustomerRepo customerRepo;
    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<Object> operationTP(TransactionTPDTO transactionTPDTO) {
        if(transactionTPDTO.getPaymentOperation().equals(TransactionTPDTO.PaymentOperationEnum.TRANSFERIR)
                && !customerRepo.existsByIban(transactionTPDTO.getDestination())){
            log.error("Destinatario no existente");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Destinatario No existe");
        }
        Optional<Customer> customer = customerRepo.findByIban(transactionTPDTO.getIban());
        if(customer.isEmpty()){
            log.error("Customer no Existe");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer No Existe");
        }
        if(transactionTPDTO.getPaymentOperation().equals(TransactionTPDTO.PaymentOperationEnum.DEPOSITAR)
                || transactionTPDTO.getPaymentOperation().equals(TransactionTPDTO.PaymentOperationEnum.RETIRAR)){
            log.error("PaymentOperation errada");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacion Errada");
        }

        ResponseEntity<TransactionTPDTO> response = restTemplate.postForEntity("http://microservicio-transaction/api/transactions/transaction/operationtp", transactionTPDTO, TransactionTPDTO.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Transaccion Realizada");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaccion realizada correctamente: " + response.getBody());
        }
        return ResponseEntity.badRequest().body("Problemas con las Transacciones quien sabe porque");
    }
}
