package cn.edu.bjtu.android.mymusicplayer.Activity;

import android.app.Application;

import cn.edu.bjtu.android.mymusicplayer.util.MyPreference;

/**
 * Created by Administrator on 2017/6/13.
 */

public class MyApplication extends Application {
    public static MyPreference mPreference;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreference = new MyPreference(getApplicationContext());

    }
}
