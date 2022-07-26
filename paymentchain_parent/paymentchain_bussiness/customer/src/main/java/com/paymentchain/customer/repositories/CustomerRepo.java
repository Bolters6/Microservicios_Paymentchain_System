package com.paymentchain.customer.repositories;

import com.paymentchain_core.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    public Optional<Customer> findByIban(String iban);
}
