package com.paymentchain.transaction.repositories;

import com.paymentchain_core.entity_transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByIban(@NotBlank  String iban);
    List<Transaction> findAllByIbanAndTransactionDateBetween(@NotBlank String iban, LocalDate transactionDate, LocalDate transactionDate2);

    }
