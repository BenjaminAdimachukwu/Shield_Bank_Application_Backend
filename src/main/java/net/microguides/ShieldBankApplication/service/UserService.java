package net.microguides.ShieldBankApplication.service;

import net.microguides.ShieldBankApplication.dto.BankResponse;
import net.microguides.ShieldBankApplication.dto.CreditDebitRequest;
import net.microguides.ShieldBankApplication.dto.EnquiryRequest;
import net.microguides.ShieldBankApplication.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest) ;
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry (EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}

