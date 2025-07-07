package net.microguides.ShieldBankApplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.microguides.ShieldBankApplication.dto.*;
import net.microguides.ShieldBankApplication.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")

@Tag(name = "User Account Management APIs")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new User and assigning account ID"

    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @Operation(
            summary = "balance Enquiry",
            description = "Given an account number, check how much the user has"

    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
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
