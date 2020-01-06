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
    final float radius = dpToPixel(80);

    float progress = 0;
    float progressSecond = 100;
    RectF arcRectF = new RectF();

    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);


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
        paintText.setTextSize(dpToPixel(40));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);

        // paint.setColor(Color.parseColor("#E91E63"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dpToPixel(15));
        // paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        paintSecond.setColor(Color.parseColor("#808080"));
        paintSecond.setStyle(Paint.Style.STROKE);
        paintSecond.setStrokeCap(Paint.Cap.ROUND);
        paintSecond.setStrokeWidth(dpToPixel(15+2));
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

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        LinearGradient shader = new LinearGradient(0, 0, getWidth(), 0,
                0x80800000, 0xffff3030, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        arcRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // paint.setColorFilter()
        canvas.drawArc(arcRectF, 135, progressSecond * 2.7f, false, paintSecond);
        canvas.drawArc(arcRectF, 135, progress * 2.7f, false, paint);
        canvas.drawText((int) progress + "%", centerX, centerY - (paintText.ascent() + paintText.descent()) / 2, paintText);
    }
}
