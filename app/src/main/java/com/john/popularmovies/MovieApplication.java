package com.john.popularmovies;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by John on 2016/6/16.
 * Init fresco
 */
public class MovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
