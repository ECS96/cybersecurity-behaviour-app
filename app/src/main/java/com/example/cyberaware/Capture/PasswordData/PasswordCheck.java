package com.example.cyberaware.Capture.PasswordData;

import com.example.cyberaware.Utils.Constants;

import java.util.regex.Pattern;
/*
    Implements a small check of strength of a password a work in progress since the capture was more important than the analysis
    Other evaluation methods of the LPSE discussed in the dissertation were planned but were not completed in time.
 */
public class PasswordCheck {

    private static int passScore = 0;
    private static int passStrength = 0;
    private static Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");

    public static int checkPass(String pass){
        passScore = 0;
        passStrength = 0;
        if( pass.length() < 8 )
            passScore = -2;
        else if( pass.length() >= 10 )
            passScore += 2;
        else
            passScore += 1;

        //if it contains one digit, add 2 to total score
        if( pass.matches("(?=.*[0-9]).*") )
            passScore += 2;

        //if it contains one lower case letter, add 2 to total score
        if( pass.matches("(?=.*[a-z]).*") )
            passScore += 2;

        //if it contains one upper case letter, add 2 to total score
        if( pass.matches("(?=.*[A-Z]).*") )
            passScore += 2;

        //if it contains one special character, add 2 to total score
        if( pass.matches("(?=.*[~!@#$%^&*()_-]).*") )
            passScore += 2;

        if(passScore <= 3) {
            passStrength = Constants.PASS_WEAKEST;
        }
        else if(passScore <= 5) {
            passStrength = Constants.PASS_WEAK;
        }
        else if (passScore <= 8) {
            passStrength = Constants.PASS_STRONG;
        }
        else if (passScore <= 10) {
            passStrength = Constants.PASS_STRONGEST;
        }

        return passStrength;
    }
}
