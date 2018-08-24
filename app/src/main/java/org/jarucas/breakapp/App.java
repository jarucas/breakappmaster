package org.jarucas.breakapp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.jarucas.breakapp.dao.User;

/**
 * Created by Javier on 23/08/2018.
 */

public class App extends MultiDexApplication {

    private static Context mContext;
    private static User mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    public static User getmUser() {
        return mUser;
    }

    public static void setmUser(User mUser) {
        App.mUser = mUser;
    }
}
