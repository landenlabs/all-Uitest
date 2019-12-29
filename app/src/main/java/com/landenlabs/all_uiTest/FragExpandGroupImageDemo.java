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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import java.util.ArrayList;

import utils.TextViewExt1;
import utils.Translation;

/**
 * Fragment which expands a group of view cells making snapshot image of selected
 * cells and expanding image.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class FragExpandGroupImageDemo extends FragBottomNavBase
        implements View.OnTouchListener {

    private TableLayout tableLayout;
    private FrameLayout overlay;
    private FrameLayout expander;
    private RadioGroup rg;
    private ArrayList<View> groupViews = new ArrayList<>();

    private int nextElevation = 1;
    private static final long ANIM_MILLI = 2000;
    private ColorStateList colorRed = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xffff0000 }    // RED
    );
    private ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xff00ff00 }    // GREEN
    );


    private static final int LAYOUT_ID = R.layout.frag_expand_group_demo;

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, LAYOUT_ID);
        setBarTitle("Group expand snapshot image");
        // setBarVisibility(View.GONE);
        initUI();

        return root;
    }

    private void initUI() {
        tableLayout = root.findViewById(R.id.page4_tableLayout);
        overlay = root.findViewById(R.id.page4_overlay);
        expander = root.findViewById(R.id.page4_expander);
        rg = root.findViewById(R.id.page4_rg);

        tableLayout.setOnTouchListener(this);
    }
    private void resetUI() {
        ViewGroup parent = (ViewGroup)root.getParent();
        parent.removeAllViews();
        root = (ViewGroup) View.inflate(getContext(), LAYOUT_ID, parent);
        nextElevation = 0;
        initUI();
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View viewTouched;

            int globalXpx = (int)event.getX();
            int globalYpx = (int)event.getY();
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            globalXpx += rect.left;
            globalYpx += rect.top;

            switch (view.getId()) {
                case R.id.page4_tableLayout:
                    viewTouched = findViewAtPosition(tableLayout, globalXpx, globalYpx);
                    if (viewTouched != null) {
                        doAction(viewTouched, tableLayout);
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * Find child view hit by touch position.
     * Set row and column in tags.
     */
    private View findViewAtPosition(View parent, int globalXpx, int globalYpx) {
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)parent;
            for (int i=0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View viewAtPosition = findViewAtPosition(child, globalXpx, globalYpx);
                if (viewAtPosition != null) {
                    // Assume Table structure, view inside row container.
                    if (viewAtPosition.getTag(R.id.tag_col) == null) {
                        viewAtPosition.setTag(R.id.tag_col, i);     // Set column first in inner container
                    } else if (viewAtPosition.getTag(R.id.tag_row) == null) {
                        viewAtPosition.setTag(R.id.tag_row, i);     // Set row in 2nd outer container.
                    }
                    return viewAtPosition;
                }
            }
            return null;
        } else {
            Rect rect = new Rect();
            parent.getGlobalVisibleRect(rect);
            if (rect.contains(globalXpx, globalYpx)) {
                return parent;
            } else {
                return null;
            }
        }
    }

    /**
     * Execute action on touched view.
     */
    private void doAction(View view, ViewGroup parent) {
        overlay.removeAllViews();
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.page1_tagRB:
                restoreGroup(parent);
                if (view.getBackground() == null) {
                    // Draw animated gradient of two possible colors.
                    view.setBackgroundResource(R.drawable.bg_anim_gradient);
                    view.setBackgroundTintList(Math.random() > 0.5 ? colorRed : colorGreen);
                    ((AnimatedVectorDrawable) view.getBackground()).start();
                } else {
                    view.setBackground(null);
                }
                break;

            case R.id.page1_grow2RB:
                if (buildExpander(parent)) {
                    // Let expander appear in default position before expanding it.
                    expander.post(new Runnable() {
                        @Override
                        public void run() {
                            expandView(expander, parent);
                        }
                    });
                }
                ((RadioButton) rg.findViewById(R.id.page1_detailsRB)).setChecked(true);
                break;
            case R.id.page1_detailsRB:
                if (groupViews.size() != 0) {
                    openDetailView(expander);
                }
                ((RadioButton)rg.findViewById(R.id.page1_tagRB)).setChecked(true);
                break;
            case R.id.page1_resetRB:
                resetUI();
                break;
        }
    }

    /*
     * Restore children.
     */
    private void restoreGroup(@NonNull ViewGroup parent) {
        for (View child : groupViews) {
            child.setTag(R.id.tag_info, null);
            child.setBackground(null);
        }
        groupViews.clear();
        expander.setVisibility(View.INVISIBLE);
    }

    private boolean buildExpander(@NonNull ViewGroup parent) {

        // Collect tagged children.
        addTaggedChildren(parent, groupViews);
        if (groupViews.isEmpty()) {
            return false;
        }

        // Compute visible boundsof children.
        Rect bounds = new Rect();
        groupViews.get(0).getGlobalVisibleRect(bounds);
        for (View child : groupViews) {
            Rect childBnd = new Rect();
            child.getGlobalVisibleRect(childBnd);
            bounds.union(childBnd);
        }

        // Set expander minimum size to hold tagged children.
        Rect expanderRect = new Rect();
        ((ViewGroup)parent.getParent()).getGlobalVisibleRect(expanderRect);

        expander.setX(bounds.left - expanderRect.left);
        expander.setY(bounds.top - expanderRect.top);
        ViewGroup.LayoutParams lp =   expander.getLayoutParams();
        lp.width = bounds.width();
        lp.height = bounds.height();
        expander.setLayoutParams(lp);
        expander.setVisibility(View.VISIBLE);

        // Get image of tagged children in parent container.
        Bitmap parentBM;
        parentBM = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas parentCanvas = new Canvas(parentBM);
        parent.draw(parentCanvas);

        Rect parentRect = new Rect();
        parent.getGlobalVisibleRect(parentRect);
        Bitmap croppedBitmap = Bitmap.createBitmap(parentBM,
                bounds.left  - parentRect.left,
                bounds.top - parentRect.top, bounds.width(), bounds.height());
        BitmapDrawable parentBD = new BitmapDrawable(getResources(), croppedBitmap);
        expander.setBackground(parentBD);

        return true;
    }

    /**
     * Collect tagged children (view with background set)..
     */
    private void addTaggedChildren(@NonNull ViewGroup parent, @NonNull ArrayList<View> childList) {
        for (int idx = 0; idx < parent.getChildCount(); idx++) {
            View child = parent.getChildAt(idx);
            if (child instanceof ViewGroup) {
                addTaggedChildren((ViewGroup)child, childList);
            } else {
                if (child.getBackground() != null) {
                    childList.add(child);
                }
            }
        }
    }

    /**
     * Set clip mode on parents. Used to allow child to expand over parent.
     */
    private void setClipChildren(@NonNull ViewGroup view, boolean toClip) {
        view.setClipChildren(toClip);
        view.setClipToPadding(toClip);
        if (view.getParent() instanceof ViewGroup) {
            setClipChildren((ViewGroup)view.getParent(), toClip);
        }
    }

    /**
     * Animate expansion of tapped view cell.
     */
    private void expandView(@Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            return;
        }

        View rootView = view.getRootView();

        setClipChildren(parent, false);

        // Record layout change and animate it slowly
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.setDuration(ANIM_MILLI);
        transitionSet.addTransition(new AutoTransition());
        transitionSet.addTransition(new Translation());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeBounds());
        TransitionManager.beginDelayedTransition((ViewGroup) rootView, transitionSet);

        view.setPivotX(view.getX() );
        view.setPivotY(view.getY() );

        final float growPercent = 1.21f;
        view.setScaleX(growPercent);
        view.setScaleY(growPercent);

        // Set elevation so it appears above its peers and parent.
        view.setElevation(nextElevation);
        nextElevation += 8;

        view.requestLayout();
        view.invalidate();
    }

    /**
     * Open a dialog in overlay to show details about tapped view.
     */
    private void  openDetailView(@NonNull View view) {
        View child = groupViews.get(0);
        int col = (Integer) child.getTag(R.id.tag_col);
        int row = (Integer) child.getTag(R.id.tag_row);

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

        float markerCenterShiftX = viewRect.centerX() - (detailLeft + detailWidthPx/2f + overlayRect.left);
        detailTv.setPointer(markerCenterShiftX);

        overlay.setElevation(nextElevation);
    }

}
