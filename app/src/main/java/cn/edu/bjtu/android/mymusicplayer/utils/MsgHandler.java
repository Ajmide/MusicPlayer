package cn.edu.bjtu.android.mymusicplayer.utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.edu.bjtu.android.mymusicplayer.Activity.PlayActivity;
import cn.edu.bjtu.android.mymusicplayer.data.MyMsg;
import cn.edu.bjtu.android.mymusicplayer.data.PlayInfo;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public enum MsgHandler {
    instance;

    private PlayActivity playActivity;
    private MediaPlayer mediaPlayer;

    private void updatePlayInfoInPlayAct(PlayInfo info){
        playActivity.setTtile(info.getSong().getTitle());
        playActivity.setEndTime(toPlayTimeFormat(info.getDuaration()));
    }

//    private void setPlayProgressTask(){
//        //log
//        Log.v("PROGERESS", "TASKSEEKBAR");
////        System.out.print("TASKSEEKBAR");
//        //cancel last one
//        playActivity.cancelTimer();
//        //
//        Timer timer = new Timer();
//        playActivity.setTimer(timer);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                playActivity.getSeekBar().setProgress(mediaPlayer.getCurrentPosition());
//                //set current time
//                playActivity.setCurTime(toPlayTimeFormat(mediaPlayer.getCurrentPosition()));
//                Log.v("PROGRRESS", "show time");
//            }
//        }, 0, mediaPlayer.getDuration());
//    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.v("MESSAGE_REV", "get msg: " + msg.what);
//            super.handleMessage(msg);
            if(msg.what == MyMsg.MSG_PLAY_PREVIOUS){

            }else if(msg.what == MyMsg.MSG_PLAY_NEXT){

            }else if(msg.what == MyMsg.MSG_PLAY){
                //It is enough
                Log.v("MESSAGE_REV", "RECEIVE Playing!");
                PlayInfo info = (PlayInfo) msg.obj;
                updatePlayInfoInPlayAct(info);
                //setPlayProgressTask();
            }
        }
    };


    public final Handler getHandler(){
        return handler;
    }

    public void setPlayActivity(PlayActivity playActivity) {
        this.playActivity = playActivity;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public String toPlayTimeFormat(int millis){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
