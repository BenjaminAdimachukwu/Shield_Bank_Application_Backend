package net.microguides.ShieldBankApplication.repository;

import net.microguides.ShieldBankApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail (String email);
    Boolean existsByAccountNumber(String accountNumber);
    Optional<User>findByAccountNumber(String accountNumber);
}
