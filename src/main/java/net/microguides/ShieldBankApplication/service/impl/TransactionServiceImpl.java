package net.microguides.ShieldBankApplication.service.impl;

import lombok.RequiredArgsConstructor;
import net.microguides.ShieldBankApplication.Enums.TransactionStatus;
import net.microguides.ShieldBankApplication.Enums.TransactionType;
import net.microguides.ShieldBankApplication.dto.TransactionDTO;
import net.microguides.ShieldBankApplication.entity.Transaction;
import net.microguides.ShieldBankApplication.repository.TransactionRepository;
import net.microguides.ShieldBankApplication.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
private final TransactionRepository transactionRepository;
    @Override
    @Transactional
    public void saveTransaction(TransactionDTO transactionDTO) {
Transaction transaction = Transaction.builder()
        .transactionType(TransactionType.valueOf(transactionDTO.transactionType()))
        .amount(transactionDTO.amount())
        .accountNumber(transactionDTO.accountNumber())
        .transactionStatus(TransactionStatus.valueOf(transactionDTO.status()))
        .timeStamp(LocalDateTime.now())
        .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");
    }
}
