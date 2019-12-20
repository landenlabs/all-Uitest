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
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landenlabs.all_uiTest.R;

/**
 * Custom TextView which has a background image which can be shifted left or right.
 * Draws a show and adds and icon to the bottom.
 */
@SuppressLint("AppCompatCustomView")
public class TextViewExt1 extends TextView {

    private BitmapShader bgShader;
    private int bgWidthPx = 0;
    private int bgHeightPx = 0;
    private float shadowSize = 20f;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix mMatrix = new Matrix();
    private float xOffsetPx = Float.NaN;

    // ---------------------------------------------------------------------------------------------
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
     * Set the relative X pixel position where pointer (horizonatal shift of background) should appear.
     * Offset zero assumes center background.
     * Value is -width/2 ... width/2
     */
    public void setPointer(float xOffsetPx) {
        this.xOffsetPx = xOffsetPx;
    }

    /**
     * Create a clamped texture bitmap which can be shifted to fill view's background.
     */
    void init() {
        BitmapDrawable bgBitmap = (BitmapDrawable)
                getResources().getDrawable(R.drawable.white_with_varrow3, getContext().getTheme());
        bgWidthPx = bgBitmap.getBitmap().getWidth();
        bgHeightPx = bgBitmap.getBitmap().getHeight();
        bgShader = new BitmapShader(bgBitmap.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        super.onDraw(canvas);
    }

    void drawBg(Canvas canvas) {
        if (bgShader == null) {
            init();
        }

        // Position the background image so the bg image center is in the center of this
        // views bounds and then shift the background by the optional user provided
        // view x pixel offset.
        mMatrix.reset();
     //   float xViewOffsetPx =  (xOffsetPx != Float.NaN) ? (xOffsetPx - getWidth()/2f) : 0;
        float xViewOffsetPx =  (xOffsetPx != Float.NaN) ? xOffsetPx : 0;
        float xShiftToCenterBg = (getWidth() - bgWidthPx)/2f;
        mMatrix.postTranslate(xShiftToCenterBg + xViewOffsetPx, 0);
        mMatrix.postTranslate(shadowSize, shadowSize);
        bgShader.setLocalMatrix(mMatrix);

        // paint.setStyle(Paint.Style.FILL);
        paint.setShader(bgShader);

        RectF shadoeCoverage = new RectF(0, 0, getRight(), getBottom()-50);
        setShadowTint(paint);
        canvas.drawRect(shadoeCoverage, paint);

        // Tint the background.
        setBlueTint(paint);
        RectF bgCoverage = new RectF(0, 0, getRight()-shadowSize, getBottom()-50-shadowSize);
        mMatrix.postTranslate(-shadowSize, -shadowSize);
        bgShader.setLocalMatrix(mMatrix);
        canvas.drawRect(bgCoverage, paint);
        paint.setShader(null);
    }

    void setBlueTint(Paint paint) {
        // Blue tint (red=.1, green=.3, blue=.6, alpha=1)
        // Matrix single array, as follows: [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]
        // When applied to a color [r, g, b, a], the resulting color is computed as (after clamping) ;
        //   R' = a*R + b*G + c*B + d*A + e;
        //   G' = f*R + g*G + h*B + i*A + j;
        //   B' = k*R + l*G + m*B + n*A + o;
        //   A' = p*R + q*G + r*B + s*A + t;
        float[] matrix = {
                .1f, 0, 0, 0, 0,  // red
                0, .3f, 0, 0, 0,  // green
                0, 0, .6f, 0, 0,  // blue
                1, 1, 1, 1, 1     // alpha
        };
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    void setShadowTint(Paint paint) {
        // Blue tint (red=.1, green=.3, blue=.6, alpha=1)
        // Matrix single array, as follows: [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]
        // When applied to a color [r, g, b, a], the resulting color is computed as (after clamping) ;
        //   R' = a*R + b*G + c*B + d*A + e;
        //   G' = f*R + g*G + h*B + i*A + j;
        //   B' = k*R + l*G + m*B + n*A + o;
        //   A' = p*R + q*G + r*B + s*A + t;
        float[] matrix = {
                .3f, 0, 0, 0, 0,  // red
                0, .3f, 0, 0, 0,  // green
                0, 0, .3f, 0, 0,  // blue
                0, 0, 0, .7f, 0   // alpha
        };
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
    }
}
