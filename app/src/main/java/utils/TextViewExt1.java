/*
 * Copyright (c) 2020 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang
 * @see https://LanDenLabs.com/
 */

package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.landenlabs.all_uiTest.R;

/**
 * Custom TextView which has a background image which can be shifted left or right.
 * Can draw a shadow and supports an icon on the bottom.
 * <p>
 *   +----------------+
 *   |          /\    |   /\ = Arrow of of marker shifted left/right
 *   |----------  ----|   #  = Tinted fill color of marker
 *   |################|
 *   |##Hello World###|   Supports normal text features.
 *   |################|
 *   |    +-----+     |   Clipped bottom area is transparent
 *   |    |  I  |     |   I = foreground icon.
 *   +----+-----+-----+
 * <p>
 *  Use foreground to attach a logo image, use foregroundGravity,
 *  paddingBottom and clipBottom to position the logo image.
 * <p>
 *  Use paddingTop, marker and markerColor to manage the Shader clamped image.
 *  See setPointer(float xOffsetPx) to shift the marker image.
 * <p>
 *  Use shadowRadius  and shadowColor to adjust the drop shadow.
 * <p>
 *  <utils.TextViewExt1
 *      android:layout_width="@dimen/page7_shader_width"
 *      android:layout_height="wrap_content"
 *      android:text="@string/msg"
 * <p>
 *
 *      android:foreground="@drawable/logo"
 *      android:foregroundGravity="bottom|center_horizontal"
 *      android:paddingBottom="80dp"
 *      app:clipBottom="40dp"
 * <p>
 *      android:paddingTop="40dp"
 *      app:marker="@drawable/bg_white_varrow"
 *      app:markerColor="#f48f"
 * <p>
 *      app:shadowRadius="6dp" />
 *
 */
@SuppressLint("AppCompatCustomView")
public class TextViewExt1 extends TextView {

    private BitmapShader bgShader;
    private int bgWidthPx = 0;
    private int bgHeightPx = 0;
    private int shadowSizePx = 20;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix mMatrix = new Matrix();
    private float xOffsetPx = Float.NaN;

    private int clipBottomPx = 50;
    private int marker = -1;
    private int markerColor = argb(1f,  .1f, .3f, .6f);
    private int shadowColor = argb(.7f,  .3f, .3f, .3f);
    private Drawable markerDrawable;
    private ColorFilter shadowColorFilter;
    private ColorFilter markerColorFilter;

    // ---------------------------------------------------------------------------------------------
    public TextViewExt1(Context context) {
        super(context);
        init(null, 0);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public TextViewExt1(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    /**
     * Set the relative X pixel position where pointer (horizonatal shift of background) should appear.
     * Offset zero assumes center background.
     * Value is -width/2 ... width/2
     */
    public void setPointer(float xOffsetPx) {
        this.xOffsetPx = xOffsetPx;
    }

    public void  setMarker(@DrawableRes int markerRes) {
        this.marker = markerRes;
        this.bgShader = null;
    }
    public void  setMaskerColor(@ColorInt int markerColor) {
        this.markerColor = markerColor;
    }

    public void setClipBottomPx(int clipBottomPx) {
        this.clipBottomPx = clipBottomPx;
    }

    public void setShadowSizePx(int shadowSizePx) {
        this.shadowSizePx = shadowSizePx;
    }
    /**
     * Create a clamped texture bitmap which can be shifted to fill view's background.
     */
  
    private void init(AttributeSet attrs, int defStyleAttr) {
        // if (isInEditMode())
        //    return;

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TextViewExt1, defStyleAttr,
                android.R.style.Widget_SeekBar);

        // mMinTic = a.getInt(R.styleable.TextViewExt1_tickMin, 0);
        // mTickUnder = a.getBoolean(R.styleable.TextViewExt1_tickUnder, false);

        marker = a.getResourceId(R.styleable.TextViewExt1_marker, -1); // android.R.drawable.ic_dialog_alert);
        markerColor = a.getColor(R.styleable.TextViewExt1_markerColor, Color.BLUE);
        shadowColor = a.getColor(R.styleable.TextViewExt1_shadowColor, 0xc0303030);
        shadowSizePx = a.getDimensionPixelSize(R.styleable.TextViewExt1_shadowRadius, shadowSizePx);
        clipBottomPx = a.getDimensionPixelSize(R.styleable.TextViewExt1_clipBottom, clipBottomPx);
        a.recycle();

        shadowColorFilter = getColorFilter(shadowColor);
        markerColorFilter = getColorFilter(markerColor);
    }

    private static ColorMatrixColorFilter getColorFilter(@ColorInt int color) {
        float r = Color.red(color)/255f;
        float g = Color.green(color)/255f;
        float b = Color.blue(color)/255f;
        float a = Color.alpha(color)/255f;

        // Matrix single array, as follows: [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]
        // When applied to a color [r, g, b, a], the resulting color is computed as (after clamping) ;
        //   R' = a*R + b*G + c*B + d*A + e;
        //   G' = f*R + g*G + h*B + i*A + j;
        //   B' = k*R + l*G + m*B + n*A + o;
        //   A' = p*R + q*G + r*B + s*A + t;
        float[] matrix = {
                r, 0, 0, 0, 0,      // red
                0, g, 0, 0, 0,      // green
                0, 0, b, 0, 0,      // blue
                0, 0, 0, a, 0       // alpha
        };
        return new ColorMatrixColorFilter(matrix);
    }

    public void init() {
        try {
            if (marker != -1) {
                markerDrawable = ResourcesCompat.getDrawable(getResources(), marker, getContext().getTheme());
            }  else {
                // Grab background as marker
                markerDrawable = getBackground();
                setBackground(null);
            }
            if (markerDrawable instanceof BitmapDrawable) {
                BitmapDrawable bgBitmap = (BitmapDrawable)markerDrawable;
                bgWidthPx = bgBitmap.getBitmap().getWidth();
                bgHeightPx = bgBitmap.getBitmap().getHeight();
                bgShader = new BitmapShader(bgBitmap.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            } else if (markerDrawable != null) {
                bgWidthPx = markerDrawable.getIntrinsicWidth();
                bgHeightPx = markerDrawable.getIntrinsicHeight();
                if (markerDrawable instanceof NinePatchDrawable ||
                        markerDrawable instanceof ColorDrawable ||
                        bgWidthPx * bgHeightPx <= 1) {
                    bgWidthPx = getWidth();
                    bgHeightPx = getHeight();
                }
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        super.onDraw(canvas);
    }

    void drawBg(Canvas canvas) {
        if (markerDrawable == null) {
            init();
        }

        if (bgWidthPx * bgHeightPx <= 0) {
            return; // Nothing to draw
        }

        // Position the background image so the bg image center is in the center of this
        // views bounds and then shift the background by the optional user provided
        // view x pixel offset.
        mMatrix.reset();
        float xViewOffsetPx =  Float.isNaN(xOffsetPx) ? 0 : xOffsetPx;
        float xShiftToCenterBg = (getWidth() - bgWidthPx)/2f;
        mMatrix.postTranslate(xShiftToCenterBg + xViewOffsetPx, 0);
        mMatrix.postTranslate(shadowSizePx, shadowSizePx);

        if (bgShader != null) {
            bgShader.setLocalMatrix(mMatrix);

            // paint.setStyle(Paint.Style.FILL);
            paint.setShader(bgShader);

            // Draw shadow
            paint.setColorFilter(shadowColorFilter);
            if (shadowSizePx > 0) {
                paint.setMaskFilter(new BlurMaskFilter(shadowSizePx, BlurMaskFilter.Blur.NORMAL));
            }
            RectF shadowCoverage = new RectF(0, 0, getWidth(), getHeight() - clipBottomPx);
            canvas.drawRect(shadowCoverage, paint);

            // Tint the background.
            paint.setColorFilter(markerColorFilter);
            paint.setMaskFilter(null);
            RectF bgCoverage = new RectF(0, 0, getWidth() - shadowSizePx, getHeight() - clipBottomPx - shadowSizePx);
            mMatrix.postTranslate(-shadowSizePx, -shadowSizePx);
            bgShader.setLocalMatrix(mMatrix);
            canvas.drawRect(bgCoverage, paint);
            paint.setShader(null);
        } else {
            // Draw shadow
            Rect shadowCoverage = new Rect(0, 0, getWidth(), getHeight() - clipBottomPx);
            markerDrawable.setBounds(shadowCoverage);
            markerDrawable.setColorFilter(shadowColorFilter);
            canvasDrawWithMatrix(canvas, markerDrawable, mMatrix);

            // Tint the background.
            Rect bgCoverage = new Rect(0, 0, getWidth() - shadowSizePx, getHeight() - clipBottomPx - shadowSizePx);
            markerDrawable.setBounds(bgCoverage);
            markerDrawable.setColorFilter(markerColorFilter);
            mMatrix.postTranslate(-shadowSizePx, -shadowSizePx);
            canvasDrawWithMatrix(canvas, markerDrawable, mMatrix);
        }
    }

    private void canvasDrawWithMatrix(Canvas canvas, Drawable drawable, Matrix matrix) {
        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.concat(matrix);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red   * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) <<  8) |
                (int) (blue  * 255.0f + 0.5f);
    }
}
