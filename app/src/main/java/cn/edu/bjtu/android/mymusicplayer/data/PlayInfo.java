package cn.edu.bjtu.android.mymusicplayer.data;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class PlayInfo {
    private Song song;
    private int duaration;
    private int curPos;

    public PlayInfo(Song song, int duaration, int pos) {
        this.song = song;
        this.duaration = duaration;
        this.curPos = pos;
    }

    public Song getSong() {
        return song;
    }

    public int getDuaration() {
        return duaration;
    }

    public int getCurPos() {
        return curPos;
    }
}
