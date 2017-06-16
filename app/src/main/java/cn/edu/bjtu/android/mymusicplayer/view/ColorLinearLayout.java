package cn.edu.bjtu.android.mymusicplayer.view;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.edu.bjtu.android.mymusicplayer.model.ColorManager;
import cn.edu.bjtu.android.mymusicplayer.model.OnColorChangedListener;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ColorLinearLayout extends LinearLayout implements
        OnColorChangedListener {
    public ColorLinearLayout(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public ColorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ColorLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onColorChanged(int color) {
        if (getBackground() != null
                && !(getBackground() instanceof ColorDrawable)) {
            getBackground().setColorFilter(new LightingColorFilter(color, 1));
        } else {
            setBackgroundColor(color);
        }
    }
    @Override
    protected void onAttachedToWindow() {
        ColorManager.getInstance().addListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        ColorManager.getInstance().removeListener(this);
        super.onDetachedFromWindow();
    }

}
