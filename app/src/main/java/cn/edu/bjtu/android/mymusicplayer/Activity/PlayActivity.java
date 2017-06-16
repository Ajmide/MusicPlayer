package cn.edu.bjtu.android.mymusicplayer.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.bjtu.android.mymusicplayer.R;
import cn.edu.bjtu.android.mymusicplayer.data.MyMsg;
import cn.edu.bjtu.android.mymusicplayer.data.PlayAction;
import cn.edu.bjtu.android.mymusicplayer.data.PlayInfo;
import cn.edu.bjtu.android.mymusicplayer.service.PlayService;
import cn.edu.bjtu.android.mymusicplayer.utils.MsgHandler;

/**
 * Created by Administrator on 2017/6/14.
 */

public class PlayActivity extends AppCompatActivity implements  Runnable{
    private TextView tv_music_title;
    private ImageView vp_play_container;
    private ImageView iv_play_back;
    private ImageView iv_pre;
    private ImageView iv_play_pause;
    private ImageView iv_next;
    private TextView tv_time_cur;
    private TextView tv_time_end;
    //
    private SeekBar seekBar;
    ///
    private boolean isPlaying;
    private boolean isSeekBarChange = false;
    private Timer timer;    //for seek bar progress

    //service
    private PlayService playService;
    private boolean serviceBind;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
            playService = binder.getService();
            serviceBind = true;

            new Thread(PlayActivity.this).start();

            Log.d("SERVICE IINIT", "service bound on PlayAct");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBind =false;

            //
            Log.d("SERVICE IINIT", "service disconnect  on PlayAct");
        }
    };
    //handler to update song play state
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            if(msg.what == MyMsg.MSG_SONG_PLAY_UPDATE){
                PlayInfo info = (PlayInfo) msg.obj;
                int progress = info.getCurPos() * 100 / info.getDuaration();
                seekBar.setProgress(progress);
                //update cur time
                String curTime = MsgHandler.instance.toPlayTimeFormat(info.getCurPos());
                setCurTime(curTime);
            }
        }
    };

    private void updatePlayStateImg(){
        if(isPlaying){
            iv_play_pause.setImageResource(R.drawable.player_btn_pause_normal);
        }else{
            iv_play_pause.setImageResource(R.drawable.player_btn_play_normal);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);
        //
        MsgHandler.instance.setPlayActivity(this);
        bindService(new Intent(this, PlayService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        //
        isPlaying = true;
        tv_music_title=(TextView) findViewById(R.id.tv_music_title);
        tv_time_cur = (TextView) findViewById(R.id.current);
        tv_time_end = (TextView) findViewById(R.id.end);
        vp_play_container=(ImageView) findViewById(R.id.vp_play_container);
        iv_play_back=(ImageView) findViewById(R.id.iv_play_back);
        iv_play_pause = (ImageView) findViewById(R.id.ib_play_start);
        iv_next = (ImageView) findViewById(R.id.ib_play_next);
        iv_pre = (ImageView) findViewById(R.id.ib_play_pre);
        seekBar = (SeekBar) findViewById(R.id.sb_play_progress);
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());
        seekBar.setProgress(0);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                seekBar.setProgress(5);
//            }
//        }, 0, 50);

        iv_play_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PlayActivity.this, MainActivity.class);
                PlayActivity.this.startActivity(intent);
            }
        });

        iv_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = !isPlaying;
                Intent intent = new Intent(PlayActivity.this, PlayService.class);
                //sendBroadcast(intent);
                if(isPlaying){
//                    iv_play_pause.setImageResource(R.drawable.player_btn_pause_normal);
                    intent.setAction(PlayAction.PLAY.toString());
                }else{
//                    iv_play_pause.setImageResource(R.drawable.player_btn_play_normal);
                    intent.setAction(PlayAction.PAUSE.toString());
                }
                startService(intent);
                updatePlayStateImg();
            }
        });

        iv_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayService.class);
                intent.setAction(PlayAction.PREVIOUS.toString());
                startService(intent);
                isPlaying = true;
                updatePlayStateImg();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayService.class);
                intent.setAction(PlayAction.NEXT.toString());
                startService(intent);
                isPlaying = true;
                updatePlayStateImg();
            }
        });


        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        setTtile(title);
        int photo=intent.getIntExtra("photo",0);
        vp_play_container.setBackgroundResource(photo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        if(serviceBind){
            unbindService(serviceConnection);
            playService.stopSelf();
        }
        cancelTimer();
    }

    @Override
    public void run() {
        while (true){
            if(!serviceBind){
                Log.v("Log", "service unbound in play act runnable");
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(!isPlaying){
                continue;
            }
            //
            int pos = playService.getSongCurPos();
            int len = playService.getSongDuration();
            if(pos == -1 || len == -1){
                break;
            }
            //
            PlayInfo info = new PlayInfo(playService.getCurSong(), len, pos);
            handler.sendMessage(handler.obtainMessage(MyMsg.MSG_SONG_PLAY_UPDATE, info));
//            int progress = pos * 100 / len;
//            seekBar.setProgress(progress);
//            //update cur time
//            String curTime = MsgHandler.instance.toPlayTimeFormat(pos);
//            setCurTime(curTime);
//            if(pos == len){
//                break;
//            }
        }
    }

    public void setTtile(String title){
        if(title == null){
            title = "unknown";
        }
        tv_music_title.setText(title);
    }

    public void setCurTime(String time){
        tv_time_cur.setText(time);
    }

    public void setEndTime(String time){
        tv_time_end.setText(time);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }


    //public

    private class MySeekBarListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChange = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChange = false;
            // TODO: 2017/6/16 0016
            //new current pos
        }
    }
}
