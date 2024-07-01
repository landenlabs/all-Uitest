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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.landenlabs.all_uiTest.R;

/**
 * Custom GridLayout which provides automatic dividers and method to lock layout to
 * prevent layout change if children change,
 * <p>
 * TODO - add ui attributes to control dividers
 * <p>
 * <com.landenlabs.all_UiDemo.Util.GridLayoutExt1
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      app:alignmentMode="alignBounds"
 *      app:columnCount="2">
 *      <p>
 * </com.landenlabs.all_UiDemo.Util.GridLayoutExt1>
 */
@SuppressWarnings("FieldCanBeLocal")
public class GridLayoutExt1 extends GridLayout {

    private Paint vPaint;
    private Paint hPaint;

    // TODO - get from property attributes.
    private final int dividerColor = Color.WHITE;
    private float hDividerWidthPx = 1;
    private float vDividerWidthPx = 1;
    private float vOffsetTopPx = 0;

    public static final int BEG = 1;
    public static final int MID = 2;
    public static final int END = 4;

    final int vDividers = MID;
    final int hDividers = MID;

    boolean locked = false;

    // ---------------------------------------------------------------------------------------------
    public GridLayoutExt1(Context context) {
        this(context, null);
    }

    public GridLayoutExt1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutExt1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Lock to prevent layout changes while expanding a cell.
     */
    public void setLock(boolean lock) {
        this.locked = lock;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (!locked) {
            super.onMeasure(widthSpec, heightSpec);
        }
    }

    @Override
    public void requestLayout() {
        if (!locked) {
            super.requestLayout();
        }
    }

    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        if (!locked) {
            super.updateViewLayout(view, params);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw dividers first, draw cells last (on top).
        drawMaxCellDividers(canvas);
        super.dispatchDraw(canvas);
    }

    /**
     * Draw dividers around cells
     */
    void drawMaxCellDividers(Canvas canvas) {

        final int count = getChildCount();
        final int colCnt = getColumnCount();
        Rect[] cell = new Rect[colCnt];

        for (int idx = 0; idx < count; idx++) {
            View view = getChildAt(idx);
            Rect colRect;
            if (idx < colCnt) {
                cell[idx] = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            }
            int col = idx % colCnt;
            colRect = cell[col];
            colRect = new Rect(
                    Math.min(colRect.left, view.getLeft()),
                    colRect.top,
                    Math.max(colRect.right, view.getRight()),
                    colRect.bottom);
            cell[col] = colRect;
        }

        int rowCnt = (count + colCnt - 1) / colCnt;
        for (int row = 0; row < rowCnt; row++) {
            int minTop = Integer.MAX_VALUE;
            int maxBot = Integer.MIN_VALUE;
            for (int col = 0; col < colCnt; col++) {
                int idx = row * colCnt + col;
                if (idx < count) {
                    View view = getChildAt(idx);
                    minTop = Math.min(minTop, view.getTop());
                    maxBot = Math.max(maxBot, view.getBottom());
                }
            }
            for (int col = 0; col < colCnt; col++) {
                Rect colRect = cell[col];

                // Vertical
                if ((vDividers & BEG) == BEG && col == 0) {
                    float vColumn = colRect.left - vDividerWidthPx / 2;
                    canvas.drawLine(vColumn, minTop + vOffsetTopPx, vColumn, maxBot, vPaint);
                }
                if ((vDividers & MID) == MID) {
                    float vColumn = colRect.right - vDividerWidthPx / 2;
                    if (col < colCnt - 1) {
                        canvas.drawLine(vColumn, minTop + vOffsetTopPx, vColumn, maxBot, vPaint);
                    }
                }
                if ((vDividers & END) == END && col == colCnt - 1) {
                    float vColumn = colRect.right - vDividerWidthPx / 2;
                    canvas.drawLine(vColumn, minTop + vOffsetTopPx, vColumn, maxBot, vPaint);
                }

                boolean drawTop = ((hDividers & BEG) == BEG && row == 0) || ((hDividers & MID) == MID && row > 0);
                if (drawTop) {
                    canvas.drawLine(colRect.left, minTop, colRect.right, minTop, hPaint);
                }
                boolean drawBot = ((hDividers & END) == END && row + 1 == rowCnt);
                if (drawBot) {
                    canvas.drawLine(colRect.left, maxBot, colRect.right, maxBot, hPaint);
                }
            }
        }
    }

    private void init() {
        vDividerWidthPx = getResources().getDimension(R.dimen.grid_v_divider);
        hDividerWidthPx = getResources().getDimension(R.dimen.grid_h_divider);
        vOffsetTopPx = getResources().getDimension(R.dimen.grid_v_divider_pad_top);

        // Vertical divider
        vPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vPaint.setColor(dividerColor);
        vPaint.setStyle(Paint.Style.STROKE);
        vPaint.setStrokeWidth(vDividerWidthPx);

        // Horizontal divider
        hPaint = new Paint(vPaint);
        hPaint.setStrokeWidth(hDividerWidthPx);
    }
}
