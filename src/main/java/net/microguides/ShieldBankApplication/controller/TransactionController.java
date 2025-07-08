package net.microguides.ShieldBankApplication.controller;

import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import net.microguides.ShieldBankApplication.entity.Transaction;
import net.microguides.ShieldBankApplication.service.BankStatement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

    private BankStatement bankStatement;

    public TransactionController(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
    }

    @GetMapping()
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam  String endDate) throws DocumentException, FileNotFoundException {

        return  bankStatement.generateBankStatement(accountNumber, startDate, endDate);
    }
}
