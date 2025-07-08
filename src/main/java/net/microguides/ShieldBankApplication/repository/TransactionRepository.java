package net.microguides.ShieldBankApplication.repository;

import net.microguides.ShieldBankApplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
