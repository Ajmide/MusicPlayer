package cn.edu.bjtu.android.mymusicplayer.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class App extends Application {
    public static Context sContext;
    public static int sScreenWidth;
    public static int sScreenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

       // startService(new Intent(this, PlayService.class));
       // startService(new Intent(this, DownloadService.class));
//        DisplayMetrics
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;
    }

}
