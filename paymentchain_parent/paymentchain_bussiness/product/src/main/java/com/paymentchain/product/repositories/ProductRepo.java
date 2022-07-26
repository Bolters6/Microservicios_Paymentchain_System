package com.paymentchain.product.repositories;

import com.paymentchain_core.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepo extends JpaRepository<Product, Long> {

}
