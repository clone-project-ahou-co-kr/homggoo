package com.hgc.homggoo.utils;

import lombok.experimental.UtilityClass;
import org.mindrot.jbcrypt.BCrypt;

@UtilityClass
public class Bcrypt {
    public String encrypt(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(
        ));
    }
    public boolean isMatch(String password, String hashed){
        return BCrypt.checkpw(password, hashed);
    }
}
