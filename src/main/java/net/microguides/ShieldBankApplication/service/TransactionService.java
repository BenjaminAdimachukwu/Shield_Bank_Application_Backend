package net.microguides.ShieldBankApplication.service;

import net.microguides.ShieldBankApplication.dto.TransactionDTO;
import net.microguides.ShieldBankApplication.entity.Transaction;

public interface TransactionService {
    void  saveTransaction(TransactionDTO transactionDTO);
}
