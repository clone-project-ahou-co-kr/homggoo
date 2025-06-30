package com.hgc.homggoo.regexes;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserRegex {
    public static Regex email = new Regex("^(.{2,50})$");
    public static Regex nickname = new Regex("^(.{2,20)$");
    public static Regex password = new Regex("^(.{8,50)$");
}
