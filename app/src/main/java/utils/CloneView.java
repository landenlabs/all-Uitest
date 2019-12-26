package utils;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CloneView extends View {

    private View mSource;

    public CloneView(Context context) {
        super(context);
    }

    public CloneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CloneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public View getSource() {
        return mSource;
    }

    public void setSource(View source) {
        mSource = source;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSource != null) {
            mSource.draw(canvas);
        }
    }

}