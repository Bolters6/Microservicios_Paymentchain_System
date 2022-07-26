package com.paymentchain.customer.mappers;

import com.paymentchain.customer.model.ProductDto;
import com.paymentchain_core.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    public ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    @Mapping(target = "id", expression = "java(conversionNumber(product.getId()))")
    public ProductDto entityToProductDto(Product product);
    public Product productDtoToEntity(ProductDto productDto);

    default BigDecimal conversionNumber(Long id){
        return BigDecimal.valueOf(id);
    }
}

