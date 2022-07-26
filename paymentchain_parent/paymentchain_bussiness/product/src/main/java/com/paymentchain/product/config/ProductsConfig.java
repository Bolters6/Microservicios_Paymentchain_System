package com.paymentchain.product.config;

import com.paymentchain.product.repositories.ProductRepo;
import com.paymentchain_core.entities.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsConfig {

    @Bean
    public CommandLineRunner commandLineRunner(ProductRepo productRepo){
        return args -> {
            if(productRepo.findAll().isEmpty()){
                productRepo.save(new Product().setCode("CA").setName("Cuenta Ahorro"));
                productRepo.save(new Product().setCode("CC").setName("Cuenta Corriente"));
                productRepo.save(new Product().setCode("TC").setName("Tarjeta de Credito"));
                productRepo.save(new Product().setCode("PC").setName("Prestamo Cliente"));
            }
        };
    }
}
