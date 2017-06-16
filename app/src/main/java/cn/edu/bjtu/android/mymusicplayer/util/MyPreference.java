package cn.edu.bjtu.android.mymusicplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.edu.bjtu.android.mymusicplayer.model.ColorManager;

/**
 * Created by Administrator on 2017/6/13.
 */

public class MyPreference {
    private final static String KEY_APP_SKIN = "key_app_skin";

    private SharedPreferences mPreferences;

    public MyPreference(Context context) {
        mPreferences = context.getSharedPreferences("motive_preference",
                Context.MODE_PRIVATE);
    }

    public void setSkinColorValue(int color) {
        mPreferences.edit().putInt(KEY_APP_SKIN, color).commit();
    }
    public int getSkinColorValue() {
        return mPreferences.getInt(KEY_APP_SKIN, ColorManager.DEFAULT_COLOR);
    }
}
