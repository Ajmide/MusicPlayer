package cn.edu.bjtu.android.mymusicplayer.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Handler;

//import cn.edu.bjtu.android.mymusicplayer.Manifest;
import cn.edu.bjtu.android.mymusicplayer.R;
import cn.edu.bjtu.android.mymusicplayer.adapter.RecyclerView_Adapter;
import cn.edu.bjtu.android.mymusicplayer.data.MyMsg;
import cn.edu.bjtu.android.mymusicplayer.data.Song;
import cn.edu.bjtu.android.mymusicplayer.model.ColorManager;
import cn.edu.bjtu.android.mymusicplayer.utils.MuiscLoader;
import cn.edu.bjtu.android.mymusicplayer.utils.StorageUtil;
import cn.edu.bjtu.android.mymusicplayer.view.SlideMenu;
import cn.edu.bjtu.android.mymusicplayer.service.PlayService;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    //
    public  static  final  String TAG_SERVICE_BIND = "cn.edu.bjtu.android.mymusicplayer.SERVICE_BIND";
    public  static  final  String TAG_PLAY_NEW_SONG = "cn.edu.bjtu.android.mymusicplayer.PLAY_NEW_SONG";
//    private static  final  int    MSG_MUSIC_LOADED = 0;

    //views
    private SlideMenu slideMenu;
    private ImageView menuImg;
    private TextView colorTheme;
    private TextView localMusic;
    private TextView about;
    //
    private List<Song> mSongList;
//    private List<Song> list;
    private List<Integer> photo;
    private RecyclerView recyclerView;
    private RecyclerView_Adapter adapter;
    private int []p={R.drawable.music1,R.drawable.music2};

    //
    private static  final int REQUEST_READ_EXTERNAL = 1;
    private PlayService playService;
    private boolean serviceBind = false;        //
    private static  boolean mAllowed = false;   //allowance for read external storage
    private MyHandler musicLoadedHandler;
    //service connection binding
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
            playService = binder.getService();
            serviceBind = true;

            Log.d("SERVICE IINIT", "service bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBind =false;

            //
            Log.d("SERVICE IINIT", "service disconnect");
        }
    };


    //load music async
    private void loadMusic(){
        Log.e("Log", "ENTER LAOD MUSIC");
        if(!mAllowed){
            Log.e("ERROR", "Get no permission for read exteenal storage!");
            return;
        }
        try{
            new  Thread(new Runnable() {
                @Override
                public void run() {
                    MuiscLoader.instance.loadMusic();
                    mSongList = MuiscLoader.instance.getSongs();
                    musicLoadedHandler.handleMessage(musicLoadedHandler.obtainMessage(MyMsg.MSG_MUSIC_LOADED));
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playAudio(int index){
        StorageUtil storageUtil = new StorageUtil(getApplicationContext());
        if(!serviceBind){
            //
//            Intent playerIntent = new Intent(this, PlayService.class);
//            playerIntent.putExtra("media", uri);
//            startService(playerIntent);
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            //
            storageUtil.stroeSongIndex(index);
            storageUtil.stroreSongs(mSongList);

            Intent playIntent = new Intent(this, PlayService.class);
            startService(playIntent);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            //log
            Log.v("INFO", "play audion not bind");
        }else{
            storageUtil.stroeSongIndex(index);
            //
            Intent intent = new Intent(TAG_PLAY_NEW_SONG);
            sendBroadcast(intent);
            //log
            Log.v("INFO", "play audion already bind");
            Log.v("INFO", "play a new song");
        }
    }

    private void checkPermisson(){
        Log.v("versiong", "Cur ver: " + Build.VERSION.SDK_INT);
        if ( Build.VERSION.SDK_INT >= 6.0 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
                //request
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }

        }
        else{
            mAllowed = true;
            loadMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_READ_EXTERNAL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mAllowed = true;
                loadMusic();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        colorTheme=(TextView) findViewById(R.id.themeColor);
        localMusic=(TextView) findViewById(R.id.localMusic);
        about=(TextView) findViewById(R.id.about);
        menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);

        menuImg.setOnClickListener(this);
        about.setOnClickListener(this);
        colorTheme.setOnClickListener(this);
        localMusic.setOnClickListener(this);
        //init Album pics ids
        initial();
        //get recycleView
        recyclerView=(RecyclerView) findViewById(R.id.recylerView) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //set Music load handler
        musicLoadedHandler = new MyHandler();
        //set context for music loader
        MuiscLoader.instance.setContext(getBaseContext());
        //
        checkPermisson();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        if(serviceBind){
            unbindService(serviceConnection);
            playService.stopSelf();
        }
    }



    public void initial(){
//        list=new ArrayList<Song>();
//        Song s=new Song(1,"下雨天","胡歌","432","xxx");
//        list.add(s);
//        Song b=new Song(1,"不是因为寂寞而想你","林俊杰","eqweqw", "fsafdas");
//        list.add(b);
//
        photo = new ArrayList<>();
        for(int i=0;i<2;i++)
            photo.add(p[i]);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_menu_btn:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;

            case R.id.about:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.about_dialog, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content

                    builder.setView(view);
                    builder.show();
                }
                break;

            case R.id.localMusic:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();

                }
                break;

            case R.id.themeColor:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ThemeActivity.class);
                startActivity(intent);
            break;


        }

    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
              if(msg.what == MyMsg.MSG_MUSIC_LOADED){
                  //
                  Log.v("Log", "Enter handling msg music loaded");
                  adapter = new RecyclerView_Adapter(mSongList,MainActivity.this,photo);
                  recyclerView.setAdapter(adapter);
                  recyclerView.setItemAnimator(new DefaultItemAnimator());
                  adapter.setOnItemClickListener(new RecyclerView_Adapter.OnItemClickListener() {
                      @Override
                      public void onItemClick(View view, int position) {
                          Log.v("Log", "Click at " + position);
                          Intent intent = new Intent();
                          intent.setClass(MainActivity.this, PlayActivity.class);
                          intent.putExtra("title",mSongList.get(position).getTitle());
                          intent.putExtra("photo",photo.get(position % photo.size()));
//                          intent.putExtra("index", position);
                          //intent.putExtra()
                          //play music
                          playAudio(position);
                          MainActivity.this.startActivity(intent);
                      }
                  });
              }
        }
    }
}