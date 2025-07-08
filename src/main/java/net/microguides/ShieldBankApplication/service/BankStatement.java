package net.microguides.ShieldBankApplication.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import net.microguides.ShieldBankApplication.dto.EmailDetails;
import net.microguides.ShieldBankApplication.entity.Transaction;
import net.microguides.ShieldBankApplication.entity.User;
import net.microguides.ShieldBankApplication.exception.AccountNotFoundException;
import net.microguides.ShieldBankApplication.repository.TransactionRepository;
import net.microguides.ShieldBankApplication.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

// retrieve list of transactions within a date range given an account
// generate a psf file of transactions
//sens the file via email
@Service
@Slf4j
public class BankStatement {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private  EmailService emailService;

    public BankStatement(TransactionRepository transactionRepository, UserRepository userRepository, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    //private static final String FILE_DIRECTORY = "/Users/user/Documents/bank_statements";
    //more dynamic
    private static final String FILE_DIRECTORY = System.getProperty("user.home") + "/Documents/bank_statements/MyStatement.pdf";



    public List<Transaction> generateBankStatement(String accountNumber, String startDateStr, String endDateStr) throws FileNotFoundException, DocumentException {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr.trim(), formatter);
        LocalDate endDate = LocalDate.parse(endDateStr.trim(), formatter);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> !transaction.getTimeStamp().isBefore(start) && !transaction.getTimeStamp().isAfter(end))
                .toList();

        User userEntity = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userEntity.getFirstName());
        stringBuilder.append(" ");
        stringBuilder.append(userEntity.getLastName());
        stringBuilder.append(" ");
        stringBuilder.append(userEntity.getOtherName());
        String customerName = stringBuilder.toString();
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize, 40, 40, 50, 50); // left, right, top, bottom
        log.info("setting size of documents");

        OutputStream outputStream = new FileOutputStream(FILE_DIRECTORY);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Shield Bank", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE)));
        bankName.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankName.setBorder(Rectangle.NO_BORDER);
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("26, walter carrington Vitoria Island, Lagos Nigeria"));
        bankAddress.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankAddress.setBorder(Rectangle.NO_BORDER);
        bankAddress.setPaddingBottom(10f);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        statementInfo.setWidthPercentage(100);
        statementInfo.setSpacingAfter(20f);

        PdfPCell customerInfo = new PdfPCell(new Phrase("start date " + start));
        PdfPCell statement = new PdfPCell( new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("end date" + end));
        stopDate.setBorder(0);

        PdfPCell name =  new PdfPCell(new Phrase("Customer Name" + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address " + userEntity.getAddress() ));
        address.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        transactionTable.setWidthPercentage(100);
        transactionTable.setSpacingBefore(10f);
        PdfPCell date  = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.LIGHT_GRAY);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBorder(0);
        status.setBackgroundColor(BaseColor.LIGHT_GRAY);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getTimeStamp().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionStatus().toString()));
        });
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(String.valueOf(start));
        statementInfo.addCell(String.valueOf(end));
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(new Paragraph("Bank Statement for " + customerName));
        document.add(new Paragraph("Period: " + startDateStr + " to " + endDateStr));
        document.add(new Paragraph("Generated on: " + LocalDateTime.now()));
        document.add(new Paragraph("\n"));

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        // Send Email with Attachment
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userEntity.getEmail())
                .subject("Your Shield Bank Statement")
                .messageBody("Hi " + customerName + ",\n\nPlease find attached your bank statement from "
                        + startDateStr + " to " + endDateStr + ".\n\nRegards,\nShield Bank")
                .attachment(FILE_DIRECTORY)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return  transactionList;



    };

    private PdfPCell createInfoCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8f);
        return cell;
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
        cell.setBackgroundColor(BaseColor.BLUE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10f);
        return cell;
    }

    private PdfPCell createBodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
