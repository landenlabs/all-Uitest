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
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.landenlabs.all_uiTest.R;

/**
 * Custom GridLayout which provides automatic dividers and method to lock layout to
 * prevent layout change if children change,
 *
 * TODO - add ui attributes to control dividers
 *
 * <com.landenlabs.all_UiDemo.Util.GridLayoutExt1
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      app:alignmentMode="alignBounds"
 *      app:columnCount="2">
 *      <p>
 * </com.landenlabs.all_UiDemo.Util.GridLayoutExt1>
 */
@SuppressWarnings("FieldCanBeLocal")
public class AnimatedCells extends View {

    // Delay between a pair of frames at a 60 FPS frame rate.
    private static final long FRAME_DELAY = 1000 / 60;

    private Rect selfR = new Rect();
    private Rect  borderR = new Rect();
    private int left, top;

    private Paint paint1, paint2, paint3;
    private BlurMaskFilter blur1 = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
    private Path path;
    private @IdRes int borderResId = -1;
    private View border;

    private float pathLen;
    private CornerPathEffect roundCorner = new CornerPathEffect(80);
    float[] dashParts = new float[]{ 0f, 0f, 0f, 0f};

    // ---------------------------------------------------------------------------------------------
    public AnimatedCells(Context context) {
        super(context);
        init(null, 0);
    }

    public AnimatedCells(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AnimatedCells(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public AnimatedCells(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        // if (isInEditMode())
        //    return;

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.AnimatedCells, defStyleAttr, 0);

        borderResId = a.getResourceId(R.styleable.AnimatedCells_borderId, -1);
        a.recycle();

        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(0xffff0000);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(18f);
        paint1.setMaskFilter(blur1);

        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(0xffff0000);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(10f);

        paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(0xffff0000);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(6f);
    }

    int percent = 0;

    //      wrap                gap
    //    +=======----------------------+
    //    #                             |
    //    #               P             |
    //    #               v             |
    //    +================-------------+
    //          draw
    //
    //      wrap, gap           draw
    //    +-------------------==========+
    //    |                   ^         #
    //    |                   P         #
    //    |                             |
    //    +-----------------------------+
    //          off

    @Override
    protected void onDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        float p = percent/100f;
        paint1.setPathEffect(getPathEffect(p+.1f, 0.1f));
        paint2.setPathEffect(getPathEffect(p+.05f, 0.05f));
        paint3.setPathEffect(getPathEffect(p+.0f, 0.05f));
        drawborders(canvas);

        postInvalidateDelayed(FRAME_DELAY);
        percent = (percent+1) % 100;
    }

    private PathEffect getPathEffect(float p, float lenP) {
        p %= 1f;
        float wrapP = Math.max(0, (p + lenP) - 1);
        float gapP =  Math.max(0, p - wrapP);
        float drawP = lenP - wrapP;
        float offP = Math.max(0, 1 - (lenP + gapP));

        dashParts[0] = wrapP * pathLen;
        dashParts[1] = gapP * pathLen;
        dashParts[2] = drawP * pathLen;
        dashParts[3] = offP * pathLen;

        DashPathEffect dashEffect = new DashPathEffect(dashParts, 0);
        return  new ComposePathEffect(roundCorner, dashEffect);
    }

    public void animateTo(@NonNull View child, float fromPercent) {
        Rect childR = new Rect();
        child.getGlobalVisibleRect(childR);
        if (selfR.contains(childR)) {

        }
    }

    public void setBorderResource(@IdRes int borderResId) {
        this.borderResId = borderResId;
        loadBorder();
    }

    private void loadBorder() {
        if (borderResId != -1) {
            border = getRootView().findViewById(borderResId);
        }
        if (border != null) {
            loadBorder(border);
        }
    }

    private void loadBorderGroup(@NonNull ViewGroup group) {
        if (group.getWidth() <= 0 || group.getHeight() <= 0) {
            return;
        }
    }

    private void loadBorder(@NonNull View view) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }

        view.setClipToOutline(false);
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup)view.getParent()).setClipChildren(false);
            ((ViewGroup)view.getParent()).setClipToPadding(false);
        }

        getGlobalVisibleRect(selfR);
        view.getGlobalVisibleRect(borderR);

        left = borderR.left - selfR.left;
        top = borderR.top - selfR.top;

        path = new Path();
        path.moveTo(left, top);
        path.lineTo(left + view.getWidth(), top);
        path.lineTo(left + view.getWidth(), top + view.getHeight());
        path.lineTo(left, top + view.getHeight());
        path.lineTo(left, top);
        path.close();

        PathMeasure pathMeas = new PathMeasure(path, false);
        pathLen = pathMeas.getLength();
    }

    /**
     * Draw animated border
     */
    void drawborders(Canvas canvas) {
        if (path == null) {
            loadBorder();
        }
        if (path != null) {
            canvas.drawPath(path, paint1);
            canvas.drawPath(path, paint2);
            canvas.drawPath(path, paint3);
        }
    }

}
