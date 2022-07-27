package com.paymentchain_core.entity_transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paymentchain_core.enums.PaymentOperation;
import com.paymentchain_core.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String iban;
    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate transactionDate;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentOperation paymentOperation;
    @NotNull
    @Min(5)
    private Double amount;
    @NotNull
    @Min(1)
    private Double fee;
    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status;
    @NotBlank
    private String description;
    @NotNull
    @Min(0)
    private Double accountAmount;
    @NotBlank
    private String destination;

    public static Double getFee(PaymentOperation paymentOperation) throws IllegalArgumentException{
        switch (paymentOperation){
            case RETIRAR -> {
                return 2.5;
            }
            case DEPOSITAR -> {
                return 1.0;
            }
            case TRANSFERIR -> {
                return 3.5;
            }
            case PAGAR -> {
                return 1.5;
            }
            default -> {
                throw new IllegalArgumentException("Hubo un Error con la Transaccion");
            }
        }
    }
}
