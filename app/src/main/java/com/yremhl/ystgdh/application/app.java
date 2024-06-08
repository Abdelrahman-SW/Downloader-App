package com.yremhl.ystgdh.application;

import android.app.Application;

import com.yremhl.ystgdh.Utilites.Utilities;

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utilities.saveDeviceLanguage(this);
    }


}
