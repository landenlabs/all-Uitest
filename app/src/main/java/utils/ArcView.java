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
 * @see http://LanDenLabs.com/
 */

package utils;

import static utils.Utils.dpToPixel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Custom View draws an animated arc with text in the middle.
 */
public class ArcView extends View {
    private final float radius = dpToPixel(80);
    private float progress = 0;
    private float progressSecond = 100;
    private final float sweepDegrees = 270;
    private final RectF arcRectF = new RectF();

    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintArcFg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintArcBg = new Paint(Paint.ANTI_ALIAS_FLAG);

    private LinearGradient shader;

    public ArcView(Context context) {
        super(context);
        init();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void  init()  {
        // Add gaps in arc at 25% 
        float arcLen = (float)(2 * Math.PI * radius * sweepDegrees / 360f);
        float gapP = 0.01f;
        float drawP = 0.25f;

        float[] dashParts = new float[6];
        dashParts[0] = (drawP - gapP/2) * arcLen;
        dashParts[1] = gapP * arcLen;
        dashParts[2] = (drawP - gapP) * arcLen;
        dashParts[3] = gapP * arcLen;
        dashParts[4] = (drawP - gapP) * arcLen;
        dashParts[5] = gapP * arcLen;
        DashPathEffect dashEffect = new DashPathEffect(dashParts, 0);

        paintText.setTextSize(dpToPixel(40));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);

        // paint.setColor(Color.parseColor("#E91E63"));
        paintArcFg.setStyle(Paint.Style.STROKE);
    //    paintArcFg.setStrokeCap(Paint.Cap.ROUND);
        paintArcFg.setStrokeWidth(dpToPixel(15));
        paintArcFg.setPathEffect(dashEffect);
        // paintArcFg.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        paintArcBg.setColor(Color.parseColor("#808080"));
        paintArcBg.setStyle(Paint.Style.STROKE);
    //    paintArcBg.setStrokeCap(Paint.Cap.ROUND);
        paintArcBg.setStrokeWidth(dpToPixel(15+2));

        // Could also draw inverse dash on top of arc.
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
    public float getSecondProgress() {
        return progressSecond;
    }

    public void setSecondProgress(float progressSecond) {
        this.progressSecond = progressSecond;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        if (shader == null) {
            shader = new LinearGradient(0, 0, getWidth(), 0,
                    0x80800000, 0xffff3030, Shader.TileMode.CLAMP);
        }

        paintArcFg.setShader(shader);
        arcRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // paint.setColorFilter()
        canvas.drawArc(arcRectF, 135, progressSecond * sweepDegrees/100, false, paintArcBg);
        canvas.drawArc(arcRectF, 135, progress * sweepDegrees/100, false, paintArcFg);
        canvas.drawText((int) progress + "%", centerX, centerY - (paintText.ascent() + paintText.descent()) / 2, paintText);
    }
}
