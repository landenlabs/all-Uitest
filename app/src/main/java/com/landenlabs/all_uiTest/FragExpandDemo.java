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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import utils.GridLayoutExt1;
import utils.TextViewExt1;
import utils.Translation;

/**
 * A simple [Fragment] subclass.
 */
public class FragExpandDemo extends FragBottomNavBase implements View.OnTouchListener {
    FloatingActionButton fab;

    ViewGroup scrollHolder;
    TableLayout tableLayout;
    GridLayoutExt1 gridLayout;

    private FrameLayout overlay;
    private RadioGroup rg;
    private int nextElevation = 1;
    private static final long ANIM_MILLI = 2000;
    ColorStateList colorRed = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xffff0000 }    // RED
    );
    ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xff00ff00 }    // GREEN
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, R.layout.frag_bottom_nav_3);
        setBarVisibility(View.GONE);
        initUI();

        return root;
    }

    private void initUI() {
        scrollHolder = root.findViewById(R.id.page3_scroll_holder);
        tableLayout = root.findViewById(R.id.page3_tableLayout);
        gridLayout = root.findViewById(R.id.page3_gridlayout);
        overlay = root.findViewById(R.id.page3_overlay);
        rg = root.findViewById(R.id.page3_rg);

        gridLayout.setOnTouchListener(this);
        tableLayout.setOnTouchListener(this);
    }
    private void resetUI() {
        ViewGroup parent = (ViewGroup)root.getParent();
        parent.removeAllViews();
        root = (ViewGroup)parent.inflate(getContext(), R.layout.frag_bottom_nav_3, parent);

        nextElevation = 0;
        initUI();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View viewTouched;
            int x = (int)event.getX();
            int y = (int)event.getY();
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            x += rect.left;
            y += rect.top;

            switch (view.getId()) {
                case R.id.page3_gridlayout:
                    viewTouched = findViewAtPosition(gridLayout, x, y);
                    if (viewTouched != null) {
                        int cnt = (Integer)viewTouched.getTag(R.id.tag_col);
                        int row = (cnt + gridLayout.getColumnCount()-1)/ gridLayout.getColumnCount();
                        int col = cnt %  gridLayout.getColumnCount();
                        viewTouched.setTag(R.id.tag_col, col);
                        viewTouched.setTag(R.id.tag_row, row);
                        doAction(viewTouched, gridLayout);
                        return true;
                    }
                    break;
                case R.id.page3_tableLayout:
                    viewTouched = findViewAtPosition(tableLayout, x, y);
                    if (viewTouched != null) {
                        doAction(viewTouched, tableLayout);
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    private View findViewAtPosition(View parent, int x, int y) {
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)parent;
            for (int i=0; i<viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View viewAtPosition = findViewAtPosition(child, x, y);
                if (viewAtPosition != null) {
                    if (viewAtPosition.getTag(R.id.tag_col) == null) {
                        viewAtPosition.setTag(R.id.tag_col, i);
                    } else if (viewAtPosition.getTag(R.id.tag_row) == null) {
                        viewAtPosition.setTag(R.id.tag_row, i);
                    }
                    return viewAtPosition;
                }
            }
            return null;
        } else {
            Rect rect = new Rect();
            parent.getGlobalVisibleRect(rect);
            if (rect.contains(x, y)) {
                return parent;
            } else {
                return null;
            }
        }
    }

    private void doAction(View view, ViewGroup parent) {
        overlay.removeAllViews();;
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.page1_tagRB:
                if (view.getBackground() == null) {
                    // Draw animated gradient of two possible colors.
                    view.setBackgroundResource(R.drawable.anim_grady1);
                    view.setBackgroundTintList(Math.random() > 0.5 ? colorRed : colorGreen);
                    ((AnimatedVectorDrawable) view.getBackground()).start();
                } else {
                    view.setBackground(null);
                }
                break;
            case R.id.page1_grow1RB:
                expandView(view, parent, 1);
                break;
            case R.id.page1_grow2RB:
                expandView(view, parent, 2);
                break;
            case R.id.page1_detailsRB:
                openDetailView(view, parent);
                break;
            case R.id.page1_resetRB:
                resetUI();
                break;
        }
    }

    private void   setClipChildren(ViewGroup view, boolean toClip) {
        view.setClipChildren(toClip);
        view.setClipToPadding(toClip);
        if (view.getParent() instanceof ViewGroup) {
            setClipChildren((ViewGroup)view.getParent(), toClip);
        }
    }

    /**
     * Animate expansion of tapped view cell.
     */
    private void expandView(View view, ViewGroup parent, int expandStyle) {
        View rootView = view.getRootView();

        if (parent.getId() == gridLayout.getId()) {
            // Prevent gridlayout from updating when children are expanded.
            gridLayout.setLock(true);
        }
        setClipChildren(parent, false);

        int numCol = TestData.WxData.columns();
        int numRow = TestData.WXDATA.length;
        int col = (Integer)view.getTag(R.id.tag_col);
        int row = (Integer)view.getTag(R.id.tag_row);

        ViewGroup.LayoutParams params = view.getLayoutParams();

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
            } else if (row + 1 == numRow) {
                view.setTranslationY(view.getHeight() - params.height);
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

    private void  openDetailView(View view, ViewGroup parent) {
        int numCol = getNumCol(parent);
        int col = (Integer) view.getTag(R.id.tag_col);
        int row = (Integer) view.getTag(R.id.tag_row);

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

        float markerCenterShiftX = viewRect.centerX() - (detailLeft + detailWidthPx/2 + overlayRect.left);
        detailTv.setPointer(markerCenterShiftX);
    }
    static int foo = -400;

    private int getNumCol(ViewGroup parent) {
        if (parent instanceof TableLayout) {
            TableLayout tableLayout = (TableLayout) parent;
            // Assume first child of tableLayout is a row (TableRow or LinearLayout)
            // and its child count is column count.
            if (tableLayout.getChildAt(0) instanceof ViewGroup) {
                return ((ViewGroup)tableLayout.getChildAt(0)).getChildCount();
            }
            return 0;
        }
        if (parent instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) parent;
            return gridLayout.getColumnCount();
        }
        return 0;
    }
    private int getNumRow(View child, ViewGroup parent) {
        if (parent instanceof TableLayout) {
            TableLayout tableLayout = (TableLayout) parent;
            return tableLayout.getChildCount();
        }
        if (parent instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) parent;
            return gridLayout.getRowCount();
        }
        return 0;
    }
    /*
    private int getCol(View child, ViewGroup parent) {
        if (parent instanceof TableLayout) {
            TableLayout tableLayout = (TableLayout) parent;
            return tableLayout.getChildCount();
        }
        if (parent instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) parent;
            return gridLayout.getColumnCount();
        }
        return 0;
    }
    private int getRow(View child, ViewGroup parent) {
        if (parent instanceof TableLayout) {
            TableLayout tableLayout = (TableLayout) parent;
            return tableLayout.getChildCount();
        }
        if (parent instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) parent;
            return gridLayout.getRowCount();
        }
        return 0;
    }
    */

}
