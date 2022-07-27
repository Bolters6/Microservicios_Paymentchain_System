package com.paymentchain.transaction.services;

import com.paymentchain.transaction.model.TransactionTPDTO;
import com.paymentchain.transaction.rest.TransactionApiDelegate;
import org.springframework.http.ResponseEntity;

public class TransactionServiceImpl implements TransactionApiDelegate {

    @Override
    public ResponseEntity<TransactionTPDTO> operationTP(TransactionTPDTO transactionTPDTO) {
        return TransactionApiDelegate.super.operationTP(transactionTPDTO);
    }
}
