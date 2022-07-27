package com.paymentchain.customer.services;

import com.paymentchain.customer.model.TransactionTPDTO;
import com.paymentchain.customer.rest.TransactionApiDelegate;
import org.springframework.http.ResponseEntity;

public class TransactionServiceImpl implements TransactionApiDelegate {
    @Override
    public ResponseEntity<TransactionTPDTO> operationTP(TransactionTPDTO transactionTPDTO) {
        return TransactionApiDelegate.super.operationTP(transactionTPDTO);
    }
}
