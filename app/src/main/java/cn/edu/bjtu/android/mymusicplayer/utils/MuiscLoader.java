package cn.edu.bjtu.android.mymusicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bjtu.android.mymusicplayer.app.App;
import cn.edu.bjtu.android.mymusicplayer.data.Song;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public enum MuiscLoader {
    instance;

    private List<Song> songs = new ArrayList<>();
    private Context ctx;

    public void setContext(Context ctx){
        this.ctx = ctx;
    }

    public void loadMusic(){
        songs.clear();
        //load

        ContentResolver contentResolver = ctx.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id =  cursor.getLong(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save
                songs.add(new Song(id, title, artist, data, album));
            }
        }
        //
        Log.v("Load", "Loading...");
        for (Song song : songs) {
            Log.v("Found", song.toString());
        }
        cursor.close();
    }

    public final List<Song> getSongs(){
        return songs;
    }

}
