package com.mecolab.memeticameandroid;


import com.google.android.gms.analytics.Tracker;

import ly.img.android.ImgLySdk;


/**
 * Created by Andres on 05-11-2015.
 */
public class MemeticameApplication extends android.app.Application {
    public static final String MESSAGE_RECEIVED_ACTION = "com.mecolab.memeticameandroid.MESSAGE";
    @Override
    public void onCreate()
    {
        super.onCreate();
        ImgLySdk.init(this);
    }

}
