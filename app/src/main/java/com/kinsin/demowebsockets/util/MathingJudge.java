package com.kinsin.demowebsockets.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/3/29.
 * PS: Not easy to write code, please indicate.
 */
public class MathingJudge {
    public static boolean EmailFormat(String email) {//邮箱判断正则表达式
        Pattern pattern = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher mc = pattern.matcher(email);
        return mc.matches();
    }
}
