package com.paymentchain.customer.services;

import com.paymentchain.customer.mappers.CustomerMapper;
import com.paymentchain.customer.model.CustomerDtoG;
import com.paymentchain.customer.model.CustomerDtoP;
import com.paymentchain.customer.repositories.CustomerRepo;
import com.paymentchain.customer.rest.CustomerApiDelegate;
import com.paymentchain_core.entities.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerApiDelegate {

    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    private final RestTemplate restTemplate;


    @Override
    public ResponseEntity<Object> addCustomer(CustomerDtoP customerDtoP) {
        if (customerRepo.existsByIban(customerDtoP.getIban())) throw new ResponseStatusException(BAD_REQUEST, "Customer ya Existente");
        Map<String, String> uriVariables = new HashMap<>();
        customerDtoP.getProducts().forEach( productDto -> {
            uriVariables.put("id", productDto.getId().toString());
            ResponseEntity<Product> responseProduct = restTemplate.getForEntity("http://microservicio-product/products/{id}", Product.class, uriVariables);
        });
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer no Existente", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerDtoG> getCustomer(BigDecimal id) {
        return customerRepo.findById(id.longValue())
                .map(customer -> ResponseEntity.ok().body(customerMapper.entityToCustomerDtoG(customer)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer no Existe"));
    }

    @Override
    public ResponseEntity<Object> putCustomer(BigDecimal id, CustomerDtoP customerDtoP) {
        return customerRepo.findById(id.longValue())
                .map(customer -> {
                    Map<String, String> uriVariables = new HashMap<>();
                    customerDtoP.getProducts().forEach( productDto -> {
                        uriVariables.put("id", productDto.getId().toString());
                        ResponseEntity<Product> responseProduct = restTemplate.getForEntity("http://microservicio-product/products/{id}", Product.class, uriVariables);
                    });
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body((Object) customerRepo.save(customerMapper.customerDtoPToEntity(customerDtoP).setId(customer.getId())));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer no Existe"));
    }
}
