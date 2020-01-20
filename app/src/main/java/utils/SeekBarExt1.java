package utils;

/*
 * Copyright (c) 2019 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  @author Dennis Lang  (3/21/2015)
 *  @see http://landenlabs.com
 *
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.landenlabs.all_uiTest.R;


/**
 * Custom SeekBar
 *
 * @see <a href="http://landenlabs.com/android"> author's web-site </a>
 *
 *  Custom attrivbutes:
 *      tickMin         ; minimum percent to start showing tick marks
 *      tickMax         ; maximjum percent to end showing tick marks
 *      tickStep        ; step percent between tick marks
 *      tickUnder       ; show tick under progress bar, defaults to over.
 *      tickColor       ; tick color, defaults #c0c08080
 *      tickWidth       ; tick width, defaults to 20px
 *
 *
 *  <utils.SeekBarExt1
 *     style="@android:style/Widget.SeekBar"
 *     android:layout_height="20dp"
 *     android:layout_columnWeight="1"
 *     android:layout_gravity="center_vertical"
 *     android:layout_margin="4dp"
 *     android:background="#8cfc"
 *     android:max="100"
 *     android:padding="4dp"
 *     android:progress="50"
 *     app:tickMax="75"
 *     app:tickMin="25"
 *     app:tickStep="25"
 *     app:tickUnder="true" />
 */
@SuppressWarnings("unused")
public class SeekBarExt1 extends androidx.appcompat.widget.AppCompatSeekBar {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private int mTickColor = 0xc0c08080;
    private float mMinTic = 0;
    private float mMaxTic = getMax();
    private float mTickStep = 10;
    private float mTickWidth = 20;
    private boolean mTickUnder = false;
    private Drawable mTickMarkDr;

    public SeekBarExt1(Context context) {
        super(context);
        initCompSeekkBar(null, 0);
    }

    public SeekBarExt1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompSeekkBar(attrs, defStyleAttr);
    }

    public SeekBarExt1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompSeekkBar(attrs, 0);
    }

    public void setTickStep(float tickStep) {
        if (mTickStep != tickStep) {
            mTickStep = tickStep;
            invalidate();
        }
    }

    public void setTickWidth(float tickWidth) {
        if (mTickWidth != tickWidth) {
            mTickWidth = tickWidth;
            invalidate();
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    private void initCompSeekkBar(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SeekBarExt1, defStyleAttr,
                android.R.style.Widget_SeekBar);

        mMinTic = a.getInt(R.styleable.SeekBarExt1_tickMin, 0);
        mMaxTic = a.getInt(R.styleable.SeekBarExt1_tickMax, getMax());
        mTickStep = a.getInt(R.styleable.SeekBarExt1_tickStep, getMax());

        mTickWidth = a.getInt(R.styleable.SeekBarExt1_tickWidth, 20);
        mTickUnder = a.getBoolean(R.styleable.SeekBarExt1_tickUnder, false);

        int tickMark = a.getResourceId(R.styleable.SeekBarExt1_tickMarker, -1);
        int defColor = (tickMark == -1) ? 0xff101010 : 0;
        mTickColor = a.getColor(R.styleable.SeekBarExt1_tickColor, defColor);

        a.recycle();

        if (tickMark != -1) {
            mTickMarkDr = getContext().getDrawable(tickMark);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //  isInEditMode()

        if (mTickUnder)
            drawTicks(canvas);

        super.onDraw(canvas);

        if (!mTickUnder)
            drawTicks(canvas);
    }

    private void drawTicks(Canvas canvas) {

        if (mTickWidth > 0 && (mTickColor != 0 || mTickMarkDr != null)) {
            float dX = mTickWidth / 2;
            float ticHeight = getHeight();

            int pixelWidth = (getWidth() - getPaddingLeft() - getPaddingRight());
            float per1 = mMinTic / getMax();
            float per2 = mMaxTic / getMax();
            float perStep =  mTickStep / getMax();


            if (perStep > 0) {
                for (float xPer = per1; xPer < per2; xPer += perStep) {
                    float x = xPer * pixelWidth + getPaddingLeft();
                    drawTick(canvas, new RectF(x - dX, 0, x + dX, ticHeight), paint);
                }
            }

            float x = per2 * pixelWidth + getPaddingLeft();
            drawTick(canvas, new RectF(x - dX, 0, x + dX, ticHeight), paint);
        }
    }

    private void drawTick(Canvas canvas, RectF rectF, Paint paint) {
        if (mTickMarkDr == null) {
            if (mTickColor != 0) {
                paint.setColor(mTickColor);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            }
            canvas.drawRect(rectF, paint);
            // canvas.drawOval(rectF, paint);
        } else {
            mTickMarkDr.setBounds((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
            if (mTickColor != 0) {
                // mTickMarkDr.setColorFilter(new PorterDuffColorFilter(mTickColor, PorterDuff.Mode.SRC_OVER));
                mTickMarkDr.setColorFilter(new PorterDuffColorFilter(mTickColor, PorterDuff.Mode.MULTIPLY));
            }
            mTickMarkDr.draw(canvas);
        }
    }
}
