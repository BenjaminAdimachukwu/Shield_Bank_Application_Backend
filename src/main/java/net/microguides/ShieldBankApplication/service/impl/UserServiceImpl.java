package net.microguides.ShieldBankApplication.service.impl;

import net.microguides.ShieldBankApplication.dto.*;
import net.microguides.ShieldBankApplication.entity.User;
import net.microguides.ShieldBankApplication.exception.AccountNotFoundException;
import net.microguides.ShieldBankApplication.exception.InsufficientBalanceException;
import net.microguides.ShieldBankApplication.repository.UserRepository;
import net.microguides.ShieldBankApplication.service.EmailService;
import net.microguides.ShieldBankApplication.service.TransactionService;
import net.microguides.ShieldBankApplication.service.UserService;
import net.microguides.ShieldBankApplication.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EmailService emailService;
    private TransactionService transactionService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionService = transactionService;
    }


    /**
     * creating an account- saving a new user into the db
     * check if user already has an accoun
     * t**/

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
            return response;
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        //send email alert to the user
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulation your account has been successful created. \n Your account details: \n Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n Account Number: "  + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
       User foundUser = userRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow(()-> new AccountNotFoundException("this account does not exist"));

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExist){
           return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber()) .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {

        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber()) .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
       userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
       userRepository.save(userToCredit);

       // save the transaction
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("DEPOSIT")
                .status("COMPLETED")
                .amount(request.getAmount())
                .build();
               transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber()) .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        //check if the amount you intend to withdraw is not more than the current account balance
        BigDecimal availableBalance = userToDebit.getAccountBalance();
        BigDecimal debitAmount = request.getAmount();

       if(availableBalance.compareTo(debitAmount) < 0){
           return  BankResponse.builder()
                   .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                   .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                   .accountInfo(null)
                   .build();
       } else {

           userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));

           userRepository.save(userToDebit);
           TransactionDTO transactionDTO = TransactionDTO.builder()
                   .accountNumber(userToDebit.getAccountNumber())
                   .transactionType("WITHDRAWAL")
                   .status("COMPLETED")
                   .amount(request.getAmount())
                   .build();
           transactionService.saveTransaction(transactionDTO);

           return    BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                   .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                   .accountInfo(AccountInfo.builder()
                           .accountNumber(request.getAccountNumber())
                           .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                           .accountBalance(userToDebit.getAccountBalance())
                           .build())
                   .build();
       }


    }

    @Override
    public BankResponse transfer(TransferRequest request) {

        // get account to debit
        // check if the amount i am debiting is not more than the current account
        // debit the account
        //get the account to credit


        //boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        // 1. Validate request parameters first
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_AMOUNT_CODE)
                    .responseMessage(AccountUtils.INVALID_AMOUNT_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // 2. Check if destination account exists
        if(!userRepository.existsByAccountNumber(request.getDestinationAccountNumber())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

       // 3. Get and validate source account
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        BigDecimal requestedAmount = request.getAmount();
        BigDecimal sourceBalance  = sourceAccountUser.getAccountBalance();

        if (requestedAmount.compareTo(sourceBalance ) > 0) {
//            throw new InsufficientBalanceException(
//                    String.format("Requested %s exceeds balance %s", requestedAmount, accountBalance)
//            );
            return  BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // 4. Perform transfer
        sourceAccountUser.setAccountBalance(sourceBalance .subtract(requestedAmount));
        userRepository.save(sourceAccountUser);

        TransactionDTO debitTransactionDTO = TransactionDTO.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("TRANSFER")
                .status("COMPLETED")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(debitTransactionDTO);


        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName()+ " " + sourceAccountUser.getOtherName();
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT!")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " was debited from your account." + " Your current Balance is " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber()).orElseThrow(()-> new AccountNotFoundException("this account does not exist"));
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(requestedAmount));
        String recipientUserName = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        userRepository.save(destinationAccountUser);

        TransactionDTO creditTransactionDTO = TransactionDTO.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("DEPOSIT")
                .status("COMPLETED")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(creditTransactionDTO);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT!")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been sent to your account from" + sourceUsername + " Your current Balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        return  BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() +" " + sourceAccountUser.getOtherName())
                        .accountNumber(sourceAccountUser.getAccountNumber())
                        .accountBalance(sourceAccountUser.getAccountBalance())
                        .build())
                .build();

    }
}
