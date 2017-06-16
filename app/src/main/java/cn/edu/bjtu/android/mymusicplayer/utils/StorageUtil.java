package cn.edu.bjtu.android.mymusicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cn.edu.bjtu.android.mymusicplayer.data.Song;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class StorageUtil {
    private static  final  String KEY_STORAGE = "cn.edu.bjtu.android.mymusicplayer.utils.STRORAGE";
    private static  final  String KEY_SONGS = "cn.edu.bjtu.android.mymusicplayer.utils.SONGS";
    private static  final  String KEY_SONG_IDNEX = "cn.edu.bjtu.android.mymusicplayer.utils.SONG_INDEX";
    //
    private SharedPreferences sharedPreferences;
    private Context ctx;

    public StorageUtil(Context ctx) {
        this.ctx = ctx;
    }

    public void stroreSongs(List<Song> songs){
        sharedPreferences = ctx.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        //
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putString(KEY_SONGS, json);
        editor.apply();
    }

    public List<Song> getSongs(){
        sharedPreferences = ctx.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_SONGS, null);
        //
        Type type = new TypeToken<List<Song>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void stroeSongIndex(int index){
        sharedPreferences = ctx.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //
        editor.putInt(KEY_SONG_IDNEX, index);
        editor.apply();
    }

    public int getSongIndex(){
        sharedPreferences = ctx.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_SONG_IDNEX, -1);
    }

    public void removeCache(){
        sharedPreferences = ctx.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

}
