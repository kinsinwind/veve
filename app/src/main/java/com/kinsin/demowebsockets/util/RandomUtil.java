package com.kinsin.demowebsockets.util;

import java.util.Random;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/3/29.
 * PS: Not easy to write code, please indicate.
 */
public class RandomUtil {
    public static String createPassCode(int len){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        //random.nextInt(10);//[0,10)
        for (int i = 0; i < len; i++) {
            buffer.append(random.nextInt(10));
        }
        return buffer.toString();
    }
}
