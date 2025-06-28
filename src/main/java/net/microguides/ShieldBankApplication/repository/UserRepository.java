package net.microguides.ShieldBankApplication.repository;

import net.microguides.ShieldBankApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail (String email);
}
