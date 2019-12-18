package com.landenlabs.all_uiTest;

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
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
 * Sample fragment demonstrate GridView with expanding cell and callouts.
 */
@SuppressWarnings("FieldCanBeLocal")
public class FragBottomNav1 extends FragBottomNavBase {

    private GridView gridview;
    private FrameLayout overlay;
    private RadioGroup rg;
    private int nextElevation = 1;
    private static final long ANIM_MILLI = 2000;
    private BitmapShader bgShader;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, R.layout.frag_bottom_nav_1);
        setBarTitle("Hourly Demo #1");

        gridview = root.findViewById(R.id.page1_gridview);
        gridview.setClipChildren(false);
        gridview.setAdapter(new Page1Adapter(getActivitySafe()));
        gridview.setClipChildren(false);

        overlay = root.findViewById(R.id.page1_overlay);
        rg = root.findViewById(R.id.page1_rg);


        return root;
    }

    private void doAction(View view, int pos) {
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.page1_tagRB:
                if (view.getBackground() == null) {
                    view.setBackgroundResource(R.drawable.anim_grady1);
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
                // gridview.invalidate();
                // gridview.forceLayout();
                gridview.setAdapter(new Page1Adapter(getActivitySafe()));
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

        int numCol = WxHourlyData.WxData.columns();
        int numRow = WxHourlyData.WXDATA.length;
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
        view.setBackgroundResource(R.drawable.red2);
        view.setElevation(nextElevation);
        nextElevation += 8;

        view.requestLayout();
        view.invalidate();
    }

    ViewGroup detailContainer;
    private void  openDetailView(View view, int pos) {
        int numCol = WxHourlyData.WxData.columns();
        int numRow = WxHourlyData.WXDATA.length;
        int col = pos % numCol;
        int row = pos / numCol;

        Rect hitRect = new Rect();
        view.getHitRect(hitRect);

        overlay.removeAllViews();
        TextViewExt1 detailTv = new TextViewExt1(getContext());
        detailTv.setText(WxHourlyData.WXDATA[row].getDetails(col));
        detailTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        detailTv.setTextColor(Color.WHITE);
        // detailTv.setBackgroundColor(0xc0000000);
        // detailTv.setBackgroundResource(R.drawable.black_with_varrow3);
        int padPx = 10;
        detailTv.setPadding(padPx, padPx*4, padPx, padPx);


        int detailWidthPx = 500;
        int detailHeightPx = ViewGroup.LayoutParams.WRAP_CONTENT;
        overlay.addView(detailTv, detailWidthPx, detailHeightPx);


        if (col < numCol / 2) {
            // Left side
            detailTv.setX(gridview.getX() + hitRect.left);
            detailTv.setY(gridview.getY() + hitRect.bottom - padPx);
        } else {
            // Right side
            detailTv.setX(gridview.getX() + view.getRight() - detailWidthPx);
            detailTv.setY(gridview.getY() + hitRect.bottom - padPx);
        }

        float viewPosX = hitRect.centerX() + gridview.getX() - detailTv.getX();
        Log.d("den", String.format("ViewX=%d + gridX=%.0f - detailX=%.0f == %.0f",
                hitRect.centerX(),  gridview.getX(),  detailTv.getX(), viewPosX));
        detailTv.setPointer(viewPosX);
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
            return WxHourlyData.size();
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

            int row = position / WxHourlyData.WxData.columns();
            switch (position % WxHourlyData.WxData.columns()) {
                default:
                case WxHourlyData.COL_TIME:
                    view = makeText(WxHourlyData.WXDATA[row].time, position);
                    break;
                case WxHourlyData.COL_TEMP:
                    view = makeText(WxHourlyData.WXDATA[row].temp, position);
                    break;
                case WxHourlyData.COL_WxICON:
                    view = makeImage(WxHourlyData.WXDATA[row].wxicon, position);
                    break;
                case WxHourlyData.COL_RAIN:
                    view = makeRain(WxHourlyData.WXDATA[row].rainPercent, position);
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
            int iconRes = percentRain > 50 ? R.drawable.probability_mixed : R.drawable.probability_rain;
            Drawable icon = ContextCompat.getDrawable(mContext, iconRes);
            textView.setCompoundDrawablePadding(40);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            return textView;
        }
    }
}
