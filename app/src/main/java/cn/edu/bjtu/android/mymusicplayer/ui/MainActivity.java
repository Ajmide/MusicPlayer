//package cn.edu.bjtu.android.mymusicplayer.ui;
//
//import android.Manifest;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.PersistableBundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.edu.bjtu.android.mymusicplayer.R;
//import cn.edu.bjtu.android.mymusicplayer.SongAdapter;
//import cn.edu.bjtu.android.mymusicplayer.data.PlayAction;
//import cn.edu.bjtu.android.mymusicplayer.data.Song;
//import cn.edu.bjtu.android.mymusicplayer.service.PlayService;
//import cn.edu.bjtu.android.mymusicplayer.utils.*;
//
//
//public class MainActivity extends AppCompatActivity {
//    private static  final int REQUEST_READ_EXTERNAL = 1;
//    private static  boolean mAllowed = false;
//
//    private List<Song> mSongList;
//    private ListView mListView;
//    private Button mBtn;
//    //
//    private PlayService playService;
//    private boolean serviceBind = false;
//
//    //
//    public  static  final  String TAG_SERVICE_BIND = "cn.edu.bjtu.android.mymusicplayer.SERVICE_BIND";
//    public  static  final  String TAG_PLAY_NEW_SONG = "cn.edu.bjtu.android.mymusicplayer.PLAY_NEW_SONG";
//
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
//            playService = binder.getService();
//            serviceBind = true;
//
//            Log.d("SERVICE IINIT", "service bound");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            serviceBind =false;
//
//            //
//            Log.d("SERVICE IINIT", "service disconnect");
//        }
//    };
//
//    private void playAudio(int index){
//        StorageUtil storageUtil = new StorageUtil(getApplicationContext());
//        if(!serviceBind){
//            //
////            Intent playerIntent = new Intent(this, PlayService.class);
////            playerIntent.putExtra("media", uri);
////            startService(playerIntent);
////            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            //
//            storageUtil.stroeSongIndex(index);
//            storageUtil.stroreSongs(mSongList);
//
//            Intent playIntent = new Intent(this, PlayService.class);
//            startService(playIntent);
//            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            //log
//            Log.v("INFO", "play audion not bind");
//        }else{
//            storageUtil.stroeSongIndex(index);
//            //
//            Intent intent = new Intent(TAG_PLAY_NEW_SONG);
//            sendBroadcast(intent);
//            //log
//            Log.v("INFO", "play audion already bind");
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        //store bind state
////        if(savedInstanceState != null)
////            savedInstanceState.putBoolean(TAG_SERVICE_BIND, serviceBind);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        //
//        mListView = (ListView) findViewById(R.id.list);
//        mBtn = (Button) findViewById(R.id.button);
//        mBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                checkPermission();
////                if(mAllowed) {
////                    getSongList();
////                }
//                Intent intent = new Intent(MainActivity.this, PlayService.class);
//                intent.setAction(PlayAction.NEXT.toString());
//                //sendBroadcast(intent);
//                startService(intent);
////                playAudio(0);
//
//            }
//        });
//        MuiscLoader.instance.setContext(this.getBaseContext());
//        //getSongList();
//        //
//        //checkPermission();
//        /*Collections.sort(mSongList, new Comparator<Song>() {
//            @Override
//            public int compare(Song o1, Song o2) {
//                return o1.getTitle().compareTo(o2.getTitle());
//            }
//        });*/
//        //
//        MuiscLoader.instance.loadMusic();
//        mSongList = MuiscLoader.instance.getSongs();
//        SongAdapter songAdapter = new SongAdapter(this, mSongList);
//        mListView.setAdapter(songAdapter);
//        playAudio(0);
//    }
//
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onRestoreInstanceState(savedInstanceState, persistentState);
////        serviceBind = savedInstanceState.getBoolean(TAG_SERVICE_BIND);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //
//        if(serviceBind){
//            unbindService(serviceConnection);
//            playService.stopSelf();
//        }
//    }
//
////    public void getSongList(){
//////        ContentResolver musicResolver = getContentResolver();
//////        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//////        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//////        if( musicCursor != null && musicCursor.moveToFirst()){
//////            //get columns
//////            int titleColumn = musicCursor.getColumnIndex
//////                    (android.provider.MediaStore.Audio.Media.TITLE);
//////            int idColumn = musicCursor.getColumnIndex
//////                    (android.provider.MediaStore.Audio.Media._ID);
//////            int artistColumn = musicCursor.getColumnIndex
//////                    (android.provider.MediaStore.Audio.Media.ARTIST);
//////            //add songs to list
//////            do {
//////                long thisId = musicCursor.getLong(idColumn);
//////                String thisTitle = musicCursor.getString(titleColumn);
//////                String thisArtist = musicCursor.getString(artistColumn);
//////                mSongList.add(new Song(thisId, thisTitle, thisArtist));
//////            } while (musicCursor.moveToNext());
//////        }
//////        Log.v("Load", "Songs: " + mSongList.size());
//////        for (Song song: mSongList) {
//////            Log.v("info", song.toString());
//////        }
////    }
//
//    public  boolean checkPermission(){
//        //Manifest.permission.READ_EXTERNAL_STORAGE
//        if ( Build.VERSION.SDK_INT >= 6.0 ){
//            Log.v("versiong", "Cur ver: " + Build.VERSION.SDK_INT);
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                    PackageManager.PERMISSION_GRANTED ){
//                //request
//                // Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                    // Show an expanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//
//                } else {
//
//                    // No explanation needed, we can request the permission.
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            REQUEST_READ_EXTERNAL);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                }
//
//            }
//        }
//        else{
//            mAllowed = true;
//        }
//
//        return true;
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case REQUEST_READ_EXTERNAL: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    mAllowed = true;
//                    //getSongList();
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//
//            }
//            default:
//                break;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}
