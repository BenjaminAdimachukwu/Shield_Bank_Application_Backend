package net.microguides.ShieldBankApplication.controller;

import net.microguides.ShieldBankApplication.dto.*;
import net.microguides.ShieldBankApplication.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceEnquiry")
    public  BankResponse balanceEnquiry ( @RequestBody EnquiryRequest request){
        return  userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
        public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public  BankResponse creditAccount(@RequestBody CreditDebitRequest request){
       return  userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount( @RequestBody CreditDebitRequest request){
      return  userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public  BankResponse transfer(@RequestBody TransferRequest request){
        return  userService.transfer(request);
    }
}
