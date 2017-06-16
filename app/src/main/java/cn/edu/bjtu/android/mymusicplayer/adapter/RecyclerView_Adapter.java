package cn.edu.bjtu.android.mymusicplayer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import java.util.List;
import cn.edu.bjtu.android.mymusicplayer.R;
import cn.edu.bjtu.android.mymusicplayer.data.Song;

/**
 * Created by Administrator on 2017/6/14.
 */

public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {
    private OnItemClickListener mOnItemClickListener = null;
    List<Song> list;
    List<Integer> p;
    Context context;

    public RecyclerView_Adapter(List<Song> list, Context context,List<Integer> p) {
        this.context = context;
        this.list = list;
        this.p=p;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        v.setOnClickListener(this);
        return holder;

    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int k=i%p.size();
        viewHolder.title.setText(list.get(i).getTitle());
        viewHolder.artist.setText(list.get(i).getArtist());
        viewHolder.album.setBackgroundResource(p.get(k));
       //将position保存在itemView的Tag中，以便点击时进行获取
       viewHolder.itemView.setTag(i);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
       if (mOnItemClickListener != null) {
           //注意这里使用getTag方法获取position
           mOnItemClickListener.onItemClick(v,(int)v.getTag());
       }
   }

    /**
     * 创建一个回调接口*/
    public interface OnItemClickListener {
       void onItemClick(View view, int position);
   }
    public void setOnItemClickListener(OnItemClickListener listener) {
       this.mOnItemClickListener = listener;
    }


}

    class ViewHolder extends RecyclerView.ViewHolder  {
        TextView title;
        TextView artist;
        ImageView album;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_music_list_title);
            artist = (TextView) itemView.findViewById(R.id.tv_music_list_artist);
            album = (ImageView) itemView.findViewById(R.id.music_list_icon);

        }


    }









