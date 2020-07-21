package com.fyp.snapshop.JavaClass;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;


public class Validation {

    public static boolean isEmpty(String incoming) {
        if (TextUtils.isEmpty(incoming)) {
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean checkMail(String mail) {
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean checkTextPattern(String txt){
        Pattern pattern = null;
        final String PASSWORD_PATTERN = "^[a-zA-Z ]*$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        if(!pattern.matcher(txt).matches()){
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean checkPassword(String password) {
        Pattern pattern = null;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        if ((!pattern.matcher(password).matches())){
            return true;
        }
        else {
            return true;
        }
    }

    public static boolean checkPhone(String txtPhone) {
         if(!Patterns.PHONE.matcher(txtPhone).matches() || (txtPhone.length() <3 || txtPhone.length() > 13)){
             return false;
         }
    return true;
    }
}
