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

import static utils.Utils.dpToPixel;

/**
 * Custom View draws an animated arc with text in the middle.
 */
public class ArcView extends View {
    private final float radius = dpToPixel(80);
    private float progress = 0;
    private float progressSecond = 100;
    private float sweepDegrees = 270;
    private RectF arcRectF = new RectF();

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
