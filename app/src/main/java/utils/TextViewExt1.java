package utils;

/*
 * Copyright (C) 2019 Dennis Lang (landenlabs@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landenlabs.all_uiTest.R;

/**
 * Custom TextView which has a background image which can be shifted left or right.
 */
@SuppressLint("AppCompatCustomView")
public class TextViewExt1 extends TextView {

    public TextViewExt1(Context context) {
        super(context);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Set the relative X pixel position where pointer (horzonatal shift of backbround) should appear.
     */
    public void setPointer(float xOffsetPx) {
        this.xOffsetPx = xOffsetPx;
    }

    BitmapShader bgShader;
    int bgWidthPx = 0;
    int bgHeightPx = 0;

    /**
     * Create a clamped texture bitmap which can be shifted to fill view's background.
     */
    void init() {
        BitmapDrawable bgBitmap = (BitmapDrawable)
                getResources().getDrawable(R.drawable.black_with_varrow, getContext().getTheme());
        bgWidthPx = bgBitmap.getBitmap().getWidth();
        bgHeightPx = bgBitmap.getBitmap().getHeight();
        bgShader = new BitmapShader(bgBitmap.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        super.onDraw(canvas);
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix mMatrix = new Matrix();
    private float xOffsetPx = -1;

    void drawBg(Canvas canvas) {
        if (bgShader == null) {
            init();
        }

        // Position the background image so the bg image center is in the center of this
        // views bounds and then shift the background by the optional user provided
        // view x pixel offset.
        mMatrix.reset();
        float xViewOffsetPx =  (xOffsetPx >= 0) ? (xOffsetPx - getWidth()/2f) : 0;
        float xShiftToCenterBg = (getWidth() - bgWidthPx)/2f;
        mMatrix.postTranslate(xShiftToCenterBg + xViewOffsetPx, 0);

        bgShader.setLocalMatrix(mMatrix);
        paint.setShader(bgShader);
        canvas.drawPaint(paint);
        paint.setShader(null);
    }
}
