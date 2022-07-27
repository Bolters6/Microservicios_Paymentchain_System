package com.paymentchain.transaction.mappers;

import com.paymentchain.transaction.model.TransactionTPDTO;
import com.paymentchain_core.dto.TransactionDto;
import com.paymentchain_core.entity_transaction.Transaction;
import com.paymentchain_core.enums.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    public TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    @Mapping(target = "fee", expression = "java(Transaction.getFee(transactionDto.getPaymentOperation()))")
    public Transaction transactionDtoToModel(TransactionDto transactionDto);
    @Mapping(target = "fee", expression = "java(Transaction.getFee(convertEnum(transactionTPDTO)))")
    @Mapping(target = "amount", expression = "java(transactionTPDTO.getAmount().doubleValue())")
    public Transaction transactionTpDtoToModel(TransactionTPDTO transactionTPDTO);

    default PaymentOperation convertEnum(TransactionTPDTO transactionTPDTO){
        return PaymentOperation.valueOf(transactionTPDTO.getPaymentOperation().getValue());
    }

}
