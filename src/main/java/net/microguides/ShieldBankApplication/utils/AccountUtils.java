package net.microguides.ShieldBankApplication.utils;

import java.time.Year;

public class AccountUtils {

    public static  final  String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created";
    public  static  final  String ACCOUNT_CREATION_SUCCESS = "002";
    public  static  final  String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";

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
