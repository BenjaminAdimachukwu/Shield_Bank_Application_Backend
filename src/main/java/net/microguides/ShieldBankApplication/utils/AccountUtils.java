package net.microguides.ShieldBankApplication.utils;

import java.time.Year;

public class AccountUtils {

    public static  final  String ACCOUNT_EXISTS_CODE = "001";
    public static  final  String ACCOUNT_NOT_EXISTS_CODE = "003";
    public static  final  String ACCOUNT_FOUND_CODE = "004";
    public static  final  String ACCOUNT_FOUND_SUCCESS = "User Account Found";
    public static  final  String ACCOUNT_NOT_EXISTS_MESSAGE = "User with the provided account does not exist";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created";
    public  static  final  String ACCOUNT_CREATION_SUCCESS = "002";
    public  static  final  String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account credited successfully";
    public  static  final  String ACCOUNT_CREDITED_SUCCESS = "005";
    public  static  final  String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";
    public  static  final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final  String INSUFFICIENT_BALANCE_MESSAGE = "insufficient Balance";
    public  static  final  String ACCOUNT_DEBITED_MESSAGE = "Account  has been successfully debited";
    public  static  final  String ACCOUNT_DEBITED_SUCCESS = "007";
    public static  final  String TRANSFER_SUCCESSFUL_CODE = "008";
    public static  final  String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer successful";
    public static final  String INVALID_AMOUNT_MESSAGE = "Amount must be positive and in valid format";;
    public static final String INVALID_AMOUNT_CODE = "000";
    public static String generateAccountNumber() {
        Year currentYear = Year.now(); // e.g., 2025
        int min = 100000;
        int max = 999999;

        // Generate a random 6-digit number
        int randNumber = (int)(Math.random() * (max - min + 1)) + min;


        //convert current year to string and the generated random number to string and concatenate
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);


        // Combine current year and random number
        //return currentYear + String.valueOf(randNumber);
        return year + randomNumber;
    }
}
