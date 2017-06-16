package cn.edu.bjtu.android.mymusicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

import cn.edu.bjtu.android.mymusicplayer.Activity.MainActivity;
import cn.edu.bjtu.android.mymusicplayer.R;
import cn.edu.bjtu.android.mymusicplayer.data.*;
import cn.edu.bjtu.android.mymusicplayer.utils.MsgHandler;
import cn.edu.bjtu.android.mymusicplayer.utils.MuiscLoader;
import cn.edu.bjtu.android.mymusicplayer.utils.StorageUtil;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class PlayService extends Service
        implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener
{
    //keys for song controlling
//    public static final String ACTION_PLAY = "cn.edu.bjtu.android.mymusicplayer.ACTION_PLAY";
//    public static final String ACTION_PAUSE = "cn.edu.bjtu.android.mymusicplayer.ACTION_PAUSE";
//    public static final String ACTION_PREVIOUS = "cn.edu.bjtu.android.mymusicplayer.ACTION_PREVIOUS";
//    public static final String ACTION_NEXT = "cn.edu.bjtu.android.mymusicplayer.ACTION_NEXT";
//    public static final String ACTION_STOP = "cn.edu.bjtu.android.mymusicplayer.ACTION_STOP";
    //media sesion controll vars
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls mediaTransControl;
    //Notification ID
    private static final int NOTIFICATION_ID = 233;

    //
    private final IBinder iBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    //private String mediaPath;
    private int resumePos;
    private int curSongIndex = -1;

    public final  Song getCurSong() {
        return curSong;
    }

    public int getSongCurPos(){
        if(mediaPlayer == null){
            return -1;
        }

        return mediaPlayer.getCurrentPosition();
    }

    public int getSongDuration(){
        if(mediaPlayer == null){
            return -1;
        }

        return mediaPlayer.getDuration();
    }

    private Song curSong;
    private List<Song>  songList;

    //for calls
    private boolean iscalled = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause it
            pause();
            //build notification
        }
    };

    //for playing new song
    private BroadcastReceiver playNewSongReceicer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = new StorageUtil(getApplicationContext()).getSongIndex();
//            List<Song> songs = MuiscLoader.instance.getSongs();
            if(index >= 0 && index < songList.size()){
                curSong = songList.get(index);
            }else{
                stopSelf();
            }
            //
            stop();
//            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    private void callStateListener(){
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
//                super.onCallStateChanged(state, incomingNumber);
                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(mediaPlayer != null){
                            pause();
                            iscalled = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(mediaPlayer != null){
                            resume();
                            iscalled = false;
                        }
                        break;

                }
            }
        };
        //
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void registerBecomingNoisyReceiver(){
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, filter);
    }

    //
    private void registerPlayNewSongReceiver(){
        IntentFilter filter = new IntentFilter(MainActivity.TAG_PLAY_NEW_SONG);
        registerReceiver(playNewSongReceicer, filter);
    }

    private void buildNotification(PlaybackStatus status){
        int notificationAction = R.drawable.pause;
        PendingIntent intent = null;
        if(status == PlaybackStatus.PAUSED){
            notificationAction = R.drawable.play;
            intent = playbackAction(PlayAction.PLAY);
        }else if(status == PlaybackStatus.PLAYING){
            notificationAction = R.drawable.pause;
            intent = playbackAction(PlayAction.PAUSE);
        }
        android.support.v4.app.NotificationCompat.Action actionPre = new android.support.v4.app.NotificationCompat.Action
                .Builder(R.drawable.previous, "previous", playbackAction(PlayAction.PREVIOUS)).build();
        android.support.v4.app.NotificationCompat.Action actionPlay = new android.support.v4.app.NotificationCompat.Action
                .Builder(notificationAction, "pause", intent).build();
        android.support.v4.app.NotificationCompat.Action actionNext = new android.support.v4.app.NotificationCompat.Action
                .Builder(R.drawable.next, "next", playbackAction(PlayAction.NEXT)).build();

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        NotificationCompat.Builder builder = (NotificationCompat.Builder)  new NotificationCompat.Builder(this)
                .setShowWhen(false)
                .setStyle(new NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.black))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.notify_bar_head)
                .setContentText(curSong.getArtist())
                .setContentTitle(curSong.getAlbum())
                .setContentInfo(curSong.getTitle())
                .addAction(actionPre)
                .addAction(actionPlay)
                .addAction(actionNext)
                /*.addAction(R.drawable.previous, "previous", playbackAction(PlayAction.PREVIOUS))
                .addAction(notificationAction, "pause", intent)
                .addAction(R.drawable.next, "next", playbackAction(PlayAction.NEXT))*/;


        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID,
                builder.build());
    }

    private void removeNotification(){
        NotificationManager manager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        manager.cancel(NOTIFICATION_ID);
    }


    private PendingIntent playbackAction(PlayAction action){
        Intent playbackIntent = new Intent(this, PlayService.class);

        switch (action){
            case  PLAY:
                playbackIntent.setAction(PlayAction.PLAY.toString());
                break;
            case  PAUSE:
                playbackIntent.setAction(PlayAction.PAUSE.toString());
                break;
            case PREVIOUS:
                playbackIntent.setAction(PlayAction.PREVIOUS.toString());
                break;
            case NEXT:
                playbackIntent.setAction(PlayAction.NEXT.toString());
                break;
            case STOP:
                playbackIntent.setAction(PlayAction.STOP.toString());
                break;
            default:
                return null;
        }
        return PendingIntent.getService(this, action.ordinal(), playbackIntent, 0);
    }

    /*send msg to playback message back to PlayActivity*/
    private void sendSongMsg(PlayAction action){
        if(PlayAction.PLAY == action){
            PlayInfo info = new PlayInfo(curSong, mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition());
            final Handler handler = MsgHandler.instance.getHandler();
            handler.sendMessage(handler.obtainMessage(MyMsg.MSG_PLAY, info));
        }
    }

    private void handlingPlaybackAction(Intent intent){
        if( intent == null || intent.getAction() == null){
            return;
        }
        //
        String str = intent.getAction();
        PlayAction action = PlayAction.toEnum(str);
        switch (action){
            case PLAY:
                mediaTransControl.play();
                break;
            case PAUSE:
                mediaTransControl.pause();
                break;
            case PREVIOUS:
                mediaTransControl.skipToPrevious();
                break;
            case NEXT:
                mediaTransControl.skipToNext();
                break;
            case STOP:
                mediaTransControl.stop();
                break;
        }
        sendSongMsg(action);

        Log.d("ERROR", "Not found for PlaybackAction: " + str);
    }

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        //set to msg handler
        MsgHandler.instance.setMediaPlayer(mediaPlayer);
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(curSong.getData());
        } catch (Exception e){
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }


    private void initMediaSession() throws RemoteException{
        Log.v("Log", "INIT MEDIASESSION");
        if(mediaSessionManager != null) {
            Log.v("Log", "session isnt null");
            return;
        }
        Log.v("Log", "Mediasession is init");

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        if(mediaSession == null){
            Log.e("ERROR", "media session is null");
        }
        mediaTransControl = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //
        updateMetaData();
        // // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback(){
//            @Override
//            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
//                super.onCommand(command, extras, cb);
//            }
//
//            @Override
//            public void onPrepare() {
//                super.onPrepare();
//            }

            @Override
            public void onPlay() {
                super.onPlay();
                resume();
                buildNotification(PlaybackStatus.PLAYING);
                //
//                sendSongMsg(PlayAction.PLAY);
            }

            @Override
            public void onPause() {
                super.onPause();
                pause();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    private void skipToNext(){
//        List<Song> songs = MuiscLoader.instance.getSongs();
        int index = (curSongIndex + 1) % songList.size();
        curSong = songList.get(index);
        //store the index
        new StorageUtil(getApplicationContext()).stroeSongIndex(index);
        //
        stop();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious(){
//        List<Song> songs = MuiscLoader.instance.getSongs();
        int index = (curSongIndex - 1 + songList.size()) % songList.size();
        curSong = songList.get(index);
        //store the index
        new StorageUtil(getApplicationContext()).stroeSongIndex(index);
        //
        stop();
        mediaPlayer.reset();
        initMediaPlayer();
    }


    private void updateMetaData(){
        Bitmap albumRrt = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
//        if(mediaSession == null){
//            Log.e("ERROR", "mediasession is null");
//            return;
//        }
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumRrt)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, curSong.getArtist())
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, curSong.getArtist())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, curSong.getTitle())
            .build()
        );
    }


    private boolean requestAudioFocus(){
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus(){
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }


    private void play(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }

    }

    private void pause(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePos = mediaPlayer.getCurrentPosition();
        }
    }

    private void stop(){
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void resume(){
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePos);
            mediaPlayer.start();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //set for msg calling
        //
        //set up listener for phone call
        callStateListener();
        registerBecomingNoisyReceiver();
        //set up for playing new song
        registerPlayNewSongReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        try {
//            //An audio file is passed to the service through putExtra();
//            mediaPath = intent.getExtras().getString("media");
//        } catch (NullPointerException e) {
//            stopSelf();
//        }
//
        Log.v("Log", "enter on start command");
        try{
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            songList = storageUtil.getSongs();
            curSongIndex = storageUtil.getSongIndex();
            if(curSongIndex >= 0 && curSongIndex < songList.size()){
                curSong = songList.get(curSongIndex);
            }else{
                stopSelf();
            }
        }catch (Exception e){
            e.printStackTrace();
            stopSelf();
        }
        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }
        if(mediaSessionManager == null){
            try{
                initMediaSession();
                initMediaPlayer();
            }catch (Exception e){
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }
        //handling incoming playback actions
        handlingPlaybackAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //
        if (mediaPlayer != null) {
            stop();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //
        if(phoneStateListener != null){
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        //remove notification
        removeNotification();
        //unregister receivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewSongReceicer);
        //clear preference cache
        new StorageUtil(getApplicationContext()).removeCache();
        //
        Log.v("SERVICE", "music service is destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch ((focusChange)){
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        sendSongMsg(PlayAction.PLAY);
        play();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public  class LocalBinder extends Binder{
        public PlayService getService(){
            return PlayService.this;
        }
    }
}
