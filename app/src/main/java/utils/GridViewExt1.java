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
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.landenlabs.all_uiTest.R;

/**
 * Custom GridView which automatically provides dividers.
 *
 * TODO - add ui attributes to control dividers
 */
public class GridViewExt1 extends GridView {

    Paint paint = new Paint();

    private static final int BEG = 1;
    private static final int MID = 2;
    private static final int END = 4;

    // Vertical dividers
    private int vDividers = MID;
    private float vDividerWidthPx = 1;
    private float vOffsetTopPx = 10;

    // Horizonatal dividers
    private float hDividerWidthPx = 1;

    public GridViewExt1(Context context) {
        super(context);
        init();
    }

    public GridViewExt1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GridViewExt1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public GridViewExt1(Context context, AttributeSet attributes) {
        super(context, attributes);
        init();
    }

    private void init() {
        // TODO - get these properties from View attributes.
        paint.setColor(Color.WHITE);
        vDividerWidthPx = getResources().getDimension(R.dimen.grid_v_divider);
        hDividerWidthPx = getResources().getDimension(R.dimen.grid_h_divider);
        vOffsetTopPx = getResources().getDimension(R.dimen.grid_v_divider_pad_top);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        final int columns = getNumColumns();
        final int count = getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = getChildAt(idx);
            int top = child.getTop();
            int bottom = child.getBottom();
            int left = child.getLeft();
            int right = child.getRight();

            paint.setStrokeWidth(hDividerWidthPx);
            int hOffset = 0;
            canvas.drawLine(left + hOffset, bottom, right - hOffset, bottom, paint);

            paint.setStrokeWidth(vDividerWidthPx);
            int col = idx % columns;
            if ((vDividers & BEG) == BEG && col == 0) {
                float vColumn = left - vDividerWidthPx/2;
                canvas.drawLine(vColumn, top+vOffsetTopPx, vColumn , bottom, paint);
            }
            if ((vDividers & MID) == MID) {
                float vColumn = right - vDividerWidthPx/2;
                if (col <  columns-1) {
                    canvas.drawLine(vColumn, top+vOffsetTopPx, vColumn , bottom, paint);
                }
            }
            if ((vDividers & END) == END && col == columns-1) {
                float vColumn = right - vDividerWidthPx/2;
                canvas.drawLine(vColumn, top+vOffsetTopPx, vColumn , bottom, paint);
            }
        }
        super.dispatchDraw(canvas);
    }
}
