package cn.edu.bjtu.android.mymusicplayer;

import org.junit.Test;

import cn.edu.bjtu.android.mymusicplayer.data.PlayAction;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testPlayAction(){
        System.out.print("Test for enum str");
        for (PlayAction action: PlayAction.values()) {
            System.out.println(action.toString());
        }
        //
        System.out.println("Test for enum");
        for (PlayAction action: PlayAction.values()) {
            int number = action.ordinal();
            System.out.println(number);
        }
        //
        System.out.println("Str to enum");
        PlayAction action = PlayAction.toEnum(PlayAction.PLAY.toString());
        System.out.println(action);

    }
}