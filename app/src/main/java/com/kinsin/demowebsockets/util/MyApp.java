package com.kinsin.demowebsockets.util;

import android.app.Application;

import com.kinsin.demowebsockets.entity.User;

public class MyApp extends Application {
    public static User currentUser =new User();

    public static User currentFriend = new User();
}