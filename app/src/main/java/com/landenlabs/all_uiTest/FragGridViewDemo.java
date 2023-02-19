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

package com.landenlabs.all_uiTest;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import java.util.Locale;

import utils.TextViewExt1;
import utils.Translation;

/**
 * Fragment demonstrate GridView with expanding cell and callouts.
 */
public class FragGridViewDemo extends FragBottomNavBase {

    private GridView gridview;
    private FrameLayout overlay;
    private RadioGroup rg;
    private int nextElevation = 1;
    private static final long ANIM_MILLI = 2000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, R.layout.frag_gridview_demo);
        setBarTitle("GridView Expanding Cell Demo");

        gridview = root.findViewById(R.id.page1_gridview);
        gridview.setClipChildren(false);
        gridview.setAdapter(new Page1Adapter(requireActivity()));

        overlay = root.findViewById(R.id.page1_overlay);
        rg = root.findViewById(R.id.page1_rg);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        // See setHasOptionsMen(true)
    }

    private ColorStateList colorRed = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xffff0000 }    // RED
    );
    private ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xff00ff00 }    // GREEN
    );

    private void doAction(View view, int pos) {
        overlay.removeAllViews();
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.page1_tagRB:
                if (view.getBackground() == null) {
                    // Draw animated gradient of two possible colors.
                    view.setBackgroundResource(R.drawable.bg_anim_gradient);
                    view.setBackgroundTintList(Math.random() > 0.5 ? colorRed : colorGreen);
                    ((AnimatedVectorDrawable) view.getBackground()).start();
                } else {
                    view.setBackground(null);
                }
                break;
            case R.id.page1_grow1RB:
                expandView(view, pos, 1);
                break;
            case R.id.page1_grow2RB:
                expandView(view, pos, 2);
                break;
            case R.id.page1_detailsRB:
                openDetailView(view, pos);
                break;
            case R.id.page1_resetRB:
                gridview.setAdapter(new Page1Adapter(requireActivity()));
                gridview.requestLayout();
                nextElevation = 0;
                break;
        }
    }

    /**
     * Animate expansion of tapped view cell.
     */
    private void expandView(View view, int pos, int expandStyle) {
        View rootView = view.getRootView();

        int numCol = TestData.WxData.columns();
        int numRow = TestData.WXDATA.length;
        int col = pos % numCol;
        int row = pos / numCol;

        GridView.LayoutParams params = (GridView.LayoutParams) view.getLayoutParams();

        // Record layout change and animate it slowly
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.setDuration(ANIM_MILLI);
        transitionSet.addTransition(new AutoTransition());
        transitionSet.addTransition(new Translation());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeBounds());
        TransitionManager.beginDelayedTransition((ViewGroup) rootView, transitionSet);

        if (expandStyle == 1) {
            float growPercent = 1.2f;
            params.width = Math.round(view.getWidth() * growPercent);
            params.height = Math.round(view.getHeight() * growPercent);
            // Change origin
            if (col + 1 == numCol) {
                view.setTranslationX(view.getWidth() - params.width);
                gridview.setClipChildren(false);
            } else if (row + 1 == numRow) {
                view.setTranslationY(view.getHeight() - params.height);
                gridview.setClipChildren(false);
            }
        } else {
            float growPercent = 0.2f;
            view.setPivotX( (col < numCol/2) ? 0 : view.getWidth());
            view.setPivotY( (row < numRow/2) ? 0 : view.getHeight());
            view.setScaleX(view.getScaleX() + growPercent);
            view.setScaleY(view.getScaleY() + growPercent);
        }

        // Change color and elevation
        view.setBackgroundResource(R.drawable.bg_red);
        view.setElevation(nextElevation);
        nextElevation += 8;

        view.requestLayout();
        view.invalidate();
    }

    private void  openDetailView(View view, int pos) {
        int numCol = TestData.WxData.columns();
        int col = pos % numCol;
        int row = pos / numCol;

        Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);

        overlay.removeAllViews();
        Rect overlayRect = new Rect();
        overlay.getGlobalVisibleRect(overlayRect);

        Rect detailRect = new Rect(viewRect.left - overlayRect.left,
                viewRect.top - overlayRect.top,
                viewRect.right - overlayRect.left,
                viewRect.bottom - overlayRect.top);

        TextViewExt1 detailTv = new TextViewExt1(getContext());
        detailTv.setMarker(R.drawable.bg_white_varrow);
        detailTv.setText(TestData.WXDATA[row].getDetails(col));
        detailTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        detailTv.setTextColor(Color.WHITE);

        Drawable icon = detailTv.getContext().getDrawable(R.drawable.wx_sun_30d);
        detailTv.setForeground(icon);
        detailTv.setForegroundGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        int padPx = 20;
        detailTv.setPadding(padPx, 40, padPx, 150);

        int detailWidthPx = 500;
        int detailHeightPx = ViewGroup.LayoutParams.WRAP_CONTENT;
        overlay.addView(detailTv, detailWidthPx, detailHeightPx);

        int margin = 10;
        int detailLeft = Math.max(margin,  detailRect.centerX() - detailWidthPx / 2);
        if (detailLeft + detailHeightPx > overlayRect.width() - margin) {
            detailLeft = overlayRect.width() - detailHeightPx - margin;
        }
        detailTv.setX(detailLeft);
        detailTv.setY(detailRect.bottom - padPx);

        float markerCenterShiftX = viewRect.centerX() - (detailLeft + detailWidthPx/2f + overlayRect.left);
        detailTv.setPointer(markerCenterShiftX);
    }

    // =============================================================================================
    //  Adapter required to fill GridView cell values.

    private final static int leftCellPadPx = 10;
    private final static int rightCellPadPx = 10;

    @SuppressWarnings("Convert2Lambda")
    class Page1Adapter extends BaseAdapter {
        private final Context mContext;
        private final int rowHeightPx;

        Page1Adapter(Context context) {
            mContext = context;

            rowHeightPx = context.getResources().getDimensionPixelSize(R.dimen.page_row_height);
        }

        public int getCount() {
            return TestData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // Create view cell per row per column using WxHourlyData as data source.
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            GridView.LayoutParams lp = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeightPx);
            int topCellPadPx = 0;

            int row = position / TestData.WxData.columns();
            switch (position % TestData.WxData.columns()) {
                default:
                case TestData.COL_TIME:
                    view = makeText(TestData.WXDATA[row].time, position);
                    break;
                case TestData.COL_TEMP:
                    view = makeText(TestData.WXDATA[row].temp, position);
                    break;
                case TestData.COL_WxICON:
                    view = makeImage(TestData.WXDATA[row].wxicon, position);
                    break;
                case TestData.COL_RAIN:
                    view = makeRain(TestData.WXDATA[row].rainPercent, position);
                    topCellPadPx = 50;  // Special case to force icon to slide down.
                    break;
            }

            view.setLayoutParams(lp);
            view.setPadding(leftCellPadPx, topCellPadPx, rightCellPadPx, 0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doAction(view, position);
                }
            });
            return view;
        }

        @SuppressWarnings("unused")
        private TextView makeText(String text, final int pos) {
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setText(text);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.WHITE);
            return textView;
        }

        @SuppressWarnings("unused")
        private ImageView makeImage(int drawableRes, final int pos) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(drawableRes);
            return imageView;
        }

        private View makeRain(float percentRain, final int pos) {
            String text = String.format(Locale.US, "%.0f%%", percentRain);
            boolean showNone = percentRain < 5;
            TextView textView = makeText(showNone ? "None" : text, pos);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, showNone ? 16:20);
            int iconRes = percentRain > 50 ? R.drawable.wx_mixed : R.drawable.wx_rain;
            Drawable icon = ContextCompat.getDrawable(mContext, iconRes);
            textView.setCompoundDrawablePadding(40);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            return textView;
        }
    }
}
