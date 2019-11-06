package com.example.cyberaware.Capture.PasswordData;


import org.mindrot.jbcrypt.BCrypt;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
/*
    BCrypt an external library part of the switchIO. https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCrypt.html
    Password hashed and analysis is run to get count across packages and strength from password strength
 */
public class Hash {

    static HashMap<String,String> mHashedPasswords = new HashMap<>();
    static int mCount = 0;
    static int mPassStrength = 0;
    static String mHashedPassword = "";

    public static Map.Entry<String,Map.Entry<Integer, Integer>> hashPass(String passwordToHash, String PkgName){
        
        if(mHashedPasswords!=null){
            for(Map.Entry hashPass : mHashedPasswords.entrySet()){
                if(BCrypt.checkpw(passwordToHash,(String)hashPass.getValue()) && !PkgName.equals(hashPass.getKey())){
                    mCount++;
                }
            }
        }


        mPassStrength = PasswordCheck.checkPass(passwordToHash);
        mHashedPassword = BCrypt.hashpw(passwordToHash, BCrypt.gensalt(12));
        mHashedPasswords.put(PkgName, mHashedPassword);


        return new AbstractMap.SimpleEntry<String, Map.Entry<Integer, Integer>>(mHashedPassword,new AbstractMap.SimpleEntry<>(mCount, mPassStrength));

    }

    public static HashMap<String, String> getmHashedPasswords(){
        return mHashedPasswords;
    }
}
