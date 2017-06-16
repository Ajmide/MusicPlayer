package cn.edu.bjtu.android.mymusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.edu.bjtu.android.mymusicplayer.data.Song;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SongAdapter extends BaseAdapter {

    private List<Song> mSongs;
    private LayoutInflater mInf;

    public SongAdapter(Context ctx, List<Song> mSongs) {
        this.mSongs = mSongs;
        mInf = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = (LinearLayout) mInf.inflate(R.layout.song, parent, false);
        TextView titleView = (TextView) layout.findViewById(R.id.song_tile);
        TextView artistView = (TextView) layout.findViewById(R.id.song_artist);
        Song curSong = mSongs.get(position);
        //set sub view text
        titleView.setText(curSong.getTitle());
        artistView.setText(curSong.getArtist());
        layout.setTag(position);

        return layout;
    }
}
