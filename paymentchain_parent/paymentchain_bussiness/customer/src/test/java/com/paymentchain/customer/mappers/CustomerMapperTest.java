package com.paymentchain.customer.mappers;

import com.paymentchain.customer.model.CustomerDtoG;
import com.paymentchain.customer.model.CustomerDtoP;
import com.paymentchain_core.entities.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = {
        CustomerMapperImpl.class
})
@ExtendWith({SpringExtension.class})
class CustomerMapperTest {

    @Autowired
    CustomerMapperImpl customerMapper;

    @Test
    void customerDtoPToEntity() {
        Customer customer = customerMapper.customerDtoPToEntity(new CustomerDtoP().name("Mario").address("it345").code("1234").iban("I1234").phone("12345").surname("Pontillo").products(null).accountBalance(BigDecimal.valueOf(150.0)));
        assertThat(customer).isEqualTo(new Customer(null, "1234", "I1234", 150.0,"Mario", "Pontillo", "12345", "it345", null));
    }

    @Test
    void entityToCustomerDtoG() {
        CustomerDtoG customerDtoG = customerMapper.entityToCustomerDtoG(new Customer(1L, "1234", "I1234",150.0, "Mario", "Pontillo", "12345", "it345", null));
        assertThat(customerDtoG).isEqualTo(new CustomerDtoG().id(BigDecimal.valueOf(1L)).name("Mario").address("it345").code("1234").iban("I1234").phone("12345").surname("Pontillo").products(null).accountBalance(BigDecimal.valueOf(150.0)));
    }
}