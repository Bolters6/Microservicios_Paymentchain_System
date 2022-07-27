package com.paymentchain_core.dto;

import com.paymentchain_core.enums.PaymentOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class TransactionTpDTO {
    @NotBlank
    private String iban;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentOperation paymentOperation;
    @NotNull
    @Min(10)
    private Double amount;
    @NotBlank
    private String description;
    @NotBlank
    private String destination;
}
