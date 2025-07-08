package net.microguides.ShieldBankApplication.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record TransactionDTO(
        String transactionType,
        BigDecimal amount,
        String accountNumber,
        String status
) { }
