package com.kinsin.demowebsockets.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 30634 on 2018/3/22.
 */

public class PreferenceUtil {

    public static String File_Name = "webSockets";

    public static String USERNAME="USERNAME";//登录账号

    public static String PASSWORD="PASSWORD";//登录密码


    public static String getString(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(File_Name, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(File_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(File_Name, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(File_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
