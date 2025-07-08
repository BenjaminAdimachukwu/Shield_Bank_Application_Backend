package net.microguides.ShieldBankApplication.entity;

import jakarta.persistence.*;
import lombok.*;
import net.microguides.ShieldBankApplication.Enums.TransactionStatus;
import net.microguides.ShieldBankApplication.Enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private BigDecimal amount;

    @Column(nullable = false)
    private  String accountNumber;

    @Version
    private Long version;

    private LocalDateTime timeStamp;

}
