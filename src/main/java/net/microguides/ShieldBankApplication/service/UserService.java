package net.microguides.ShieldBankApplication.service;

import net.microguides.ShieldBankApplication.dto.BankResponse;
import net.microguides.ShieldBankApplication.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest) ;
}

