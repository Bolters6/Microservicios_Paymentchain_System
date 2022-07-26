package com.paymentchain.customer.mappers;

import com.paymentchain.customer.model.CustomerDtoG;
import com.paymentchain.customer.model.CustomerDtoP;
import com.paymentchain_core.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    public CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    public Customer customerDtoPToEntity(CustomerDtoP customerDtoP);
    public CustomerDtoP entityToCustomerDtoP(Customer customer);
    public Customer customerDtoGToEntity(CustomerDtoG customerDtoG);
    @Mapping(target = "id", expression = "java(conversionNumber(customer.getId()))")
    @Mapping(target = "accountBalance", defaultValue = "BigDecimal.valueOf(customer.getAccountBalance())")
    public CustomerDtoG entityToCustomerDtoG(Customer customer);

    default BigDecimal conversionNumber(Long id){
        return BigDecimal.valueOf(id);
    }
}
