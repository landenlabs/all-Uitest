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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
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
 * Animate border cell lines by using geometry from another container.  The border
 * container is used to generate a path around the container and to individual view
 * children cells.
 * <p>
 * The animation draws a glow line which runs around the outside of the border container
 * and/or travels into the center to a selected child view.
 * <p>
 * TODO - complete class implementation.
 *
 * <pre>
 *  &lt;!-- AnimatedCells is over its border source -->
 *  &lt;utils.AnimatedCells
 *      android:layout_alignTop="@+id/page6_tableLayout"
 *      android:layout_alignBottom="@+id/page6_tableLayout"
 *      android:layout_alignStart="@+id/page6_tableLayout"
 *      android:layout_alignEnd="@+id/page6_tableLayout"
 *      android:layout_width="0dp"
 *      android:layout_height="0dp"
 *      app:borderId="@+id/page6_tableLayout"
 *      />
 *  </pre>
 */
@SuppressWarnings("FieldCanBeLocal")
public class AnimatedCells extends View {

    // Rate to update animation.
    private static final long FRAME_DELAY = 1000 / 60;

    private final Rect selfR = new Rect();
    private final Rect  borderR = new Rect();
    private int left, top;

    private Paint paintBlur, paintColor;
    private final BlurMaskFilter blur1 = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
    private Path path;
    private @IdRes int borderResId = -1;
    private View border;

    private float pathLen;
    private final CornerPathEffect roundCorner = new CornerPathEffect(80);
    final float[] dashParts = new float[]{ 0f, 0f, 0f, 0f};

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

        paintBlur = new Paint();
    //    paintBlur.setAntiAlias(false);
    //    paintBlur.setDither(true);
        paintBlur.setColor(Color.argb(200, 240, 140, 75));
        paintBlur.setStyle(Paint.Style.STROKE);
        paintBlur.setStrokeJoin(Paint.Join.ROUND);
        paintBlur.setStrokeCap(Paint.Cap.ROUND);
        paintBlur.setStrokeWidth(30f);
        paintBlur.setMaskFilter(blur1);

        paintColor = new Paint();
        paintColor.setColor(Color.argb(248, 255, 255, 255));
        paintColor.setStyle(Paint.Style.STROKE);
        paintColor.setStrokeJoin(Paint.Join.ROUND);
        paintColor.setStrokeCap(Paint.Cap.ROUND);
        paintColor.setStrokeWidth(10f);
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


    @SuppressWarnings("SameParameterValue")
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        float p = percent/100f;
        paintBlur.setPathEffect(getPathEffect(p+.0f, 0.3f));
        paintColor.setPathEffect(getPathEffect(p+.0f, 0.3f));
        drawborders(canvas);

        postInvalidateDelayed(FRAME_DELAY);
        percent = (percent+1) % 100;
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
            canvas.drawPath(path, paintBlur);
            canvas.drawPath(path, paintColor);
        }
    }

}
