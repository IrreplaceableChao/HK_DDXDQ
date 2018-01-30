package com.hekang.hkcxn.util;

import android.app.Application;
import android.content.Context;

public class CrashApplication extends Application {
    private static Context gContext;
    private static CrashApplication singleton;


    @Override
    public void onCreate() {
        super.onCreate();
//        存崩溃日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        gContext = getApplicationContext();
        singleton = this;

    }
    public static CrashApplication getInstance() {
        return singleton;
    }
    public Context applicationContext() {
        return gContext;
    }
}
