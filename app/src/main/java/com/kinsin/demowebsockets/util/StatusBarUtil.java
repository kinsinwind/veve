package com.kinsin.demowebsockets.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/3.
 * PS: Not easy to write code, please indicate.
 */
public class StatusBarUtil {
    public static void immersion(Activity activity){
        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
