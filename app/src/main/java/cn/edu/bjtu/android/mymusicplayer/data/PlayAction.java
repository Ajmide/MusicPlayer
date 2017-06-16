package cn.edu.bjtu.android.mymusicplayer.data;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public enum PlayAction {
    PLAY,
    PAUSE,
    PREVIOUS,
    NEXT,
    STOP;

    public static PlayAction toEnum(String str){
        if(str.equals(PLAY.toString())){
            return PLAY;
        }else if(str.equals(PAUSE.toString())){
            return PAUSE;
        }else if(str.equals(PREVIOUS.toString())){
            return PREVIOUS;
        }else if(str.equals(NEXT.toString())){
            return NEXT;
        }else if(str.equals(STOP.toString())){
            return STOP;
        }

//        System.out.println("[" + str + "]");
//        System.out.println("[" + PLAY + "]");
        //
        return null;
    }

    @Override
    public String toString() {
        return PlayAction.class.getName() + "." + name();
    }
}
