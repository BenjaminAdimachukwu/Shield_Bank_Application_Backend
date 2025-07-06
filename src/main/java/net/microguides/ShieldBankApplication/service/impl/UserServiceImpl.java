package net.microguides.ShieldBankApplication.service.impl;

import net.microguides.ShieldBankApplication.dto.*;
import net.microguides.ShieldBankApplication.entity.User;
import net.microguides.ShieldBankApplication.repository.UserRepository;
import net.microguides.ShieldBankApplication.service.EmailService;
import net.microguides.ShieldBankApplication.service.UserService;
import net.microguides.ShieldBankApplication.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
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
       User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

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

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
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
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
       userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
       userRepository.save(userToCredit);

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
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

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
}
