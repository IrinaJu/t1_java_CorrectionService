package ru.t1.java.demo.mapper;

import org.mapstruct.*;
import ru.t1.java.demo.model.Transaction;

import ru.t1.java.demo.model.dto.TransactionDTO;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    Transaction toEntity(TransactionDTO transactionDto);

    TransactionDTO toDto(Transaction transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Transaction partialUpdate(TransactionDTO transactionDto, @MappingTarget Transaction transaction);
}
