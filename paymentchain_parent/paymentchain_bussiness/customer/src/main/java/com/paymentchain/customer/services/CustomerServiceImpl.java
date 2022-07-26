package com.paymentchain.customer.services;

import com.paymentchain.customer.mappers.CustomerMapper;
import com.paymentchain.customer.model.CustomerDtoG;
import com.paymentchain.customer.model.CustomerDtoP;
import com.paymentchain.customer.repositories.CustomerRepo;
import com.paymentchain.customer.rest.CustomerApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerApiDelegate {

    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;

    @Override
    public ResponseEntity<Object> addCustomer(CustomerDtoP customerDtoP) {
        customerRepo.save(customerMapper.customerDtoPToEntity(customerDtoP));
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer Creado");
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(BigDecimal id) {
        try {
            customerRepo.deleteById(id.longValue());
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            log.error("Customer con id: {} no existe", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerDtoG> getCustomer(BigDecimal id) {
        return customerRepo.findById(id.longValue())
                .map(customer -> ResponseEntity.ok().body(customerMapper.entityToCustomerDtoG(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Object> putCustomer(BigDecimal id, CustomerDtoP customerDtoP) {
        return customerRepo.findById(id.longValue())
                .map(customer ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body((Object) customerRepo.save(customerMapper.customerDtoPToEntity(customerDtoP).setId(customer.getId()))))
                .orElse(ResponseEntity.notFound().build());
    }
}
