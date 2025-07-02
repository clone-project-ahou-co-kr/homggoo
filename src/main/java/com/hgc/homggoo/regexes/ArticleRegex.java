package com.hgc.homggoo.regexes;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ArticleRegex {
    public static Regex title = new Regex("^(.{1,60})$");
    public static Regex content = new Regex("(?s)^.{1,100000}$");
}
