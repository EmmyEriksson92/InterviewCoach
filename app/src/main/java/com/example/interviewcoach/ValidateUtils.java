package com.example.interviewcoach;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Program for validating name, phone number, password & email address.
 *
 * @author Emmy
 */
public class ValidateUtils {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,20}$";
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";


    //Method for validating email adress
    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    //Method for validating name.
    public static boolean validateName(String name) {
        return !TextUtils.isEmpty(name);
    }


    //Method for validating phone number.
    public static boolean validatePhoneNb(String phoneNumber) {
        return TextUtils.isDigitsOnly(phoneNumber.trim());
    }

    /*Method for validating password.

    Password must contain at least one lowercase character,
    * one uppercase character, one digit, one special character,
    *  and a length between 8 to 20.  */

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
