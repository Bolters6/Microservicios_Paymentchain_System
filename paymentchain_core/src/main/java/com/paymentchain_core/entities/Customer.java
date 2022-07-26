package com.paymentchain_core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@Entity
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String code;
    @NotBlank
    private String iban;
    @NotNull
    @Min(50)
    private Double accountBalance;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotEmpty
    @NotNull
    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "customer_product_rel", joinColumns = {
            @JoinColumn(name = "customer_id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "product_id")
    })
    private Set<Product> products;

}
