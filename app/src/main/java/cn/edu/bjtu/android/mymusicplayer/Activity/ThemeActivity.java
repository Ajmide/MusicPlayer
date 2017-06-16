package cn.edu.bjtu.android.mymusicplayer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.edu.bjtu.android.mymusicplayer.R;
import cn.edu.bjtu.android.mymusicplayer.model.ColorManager;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ThemeActivity extends AppCompatActivity {
    private final int[] layouts = { R.id.skin_01, R.id.skin_02, R.id.skin_03,
            R.id.skin_04, R.id.skin_05 };
    private ImageView title_bar_menu_btn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_choice);
        title_bar_menu_btn=(ImageView)findViewById(R.id.title_bar_menu_btn);
        title_bar_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ThemeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        initView();
    }
    private void initView() {
        int colorArr[] = ColorManager.getInstance().getSkinColor(this);
        for (int i = 0; i < layouts.length; i++) {
            View view = findViewById(layouts[i]);
            View color = view.findViewById(R.id.motive_item_color);
            View selected = view.findViewById(R.id.motive_item_selected);
            color.setBackgroundColor(colorArr[i]);
            if (colorArr[i] == MyApplication.mPreference.getSkinColorValue()) {
                selected.setVisibility(View.VISIBLE);
            }
            color.setOnClickListener(new OnSkinColorClickListener(i));
        }
    }

    class OnSkinColorClickListener implements View.OnClickListener {

        private int position;

        public OnSkinColorClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            for (int i = 0; i < layouts.length; i++) {
                View view = findViewById(layouts[i]);
                View selected = view.findViewById(R.id.motive_item_selected);
                selected.setVisibility(i == position ? View.VISIBLE : View.GONE);
                ColorManager.getInstance().setSkinColor(ThemeActivity.this,
                        position);
            }
        }
    }
}
