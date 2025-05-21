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
 * @see https://LanDenLabs.com/
 */

package com.landenlabs.all_uiTest;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import java.util.ArrayList;

import utils.TextViewExt1;
import utils.Translation;

/**
 * Fragment which demonstrates expanding a group of view cells by re-parenting and then using a translation
 * animation.
 */
@SuppressWarnings({"unused"})
public class FragExpandGroupViewDemo extends FragBottomNavBase implements View.OnTouchListener {
    private TableLayout tableLayout;
    private FrameLayout overlay;
    private FrameLayout expander;
    private RadioGroup rg;

    private int nextElevation = 1;
    private static final long ANIM_MILLI = 2000;
    private final ColorStateList colorRed = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xffff0000 }    // RED
    );
    private final ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xff00ff00 }    // GREEN
    );
    private static class TagInfo {
        int idx; // index into parth
        Rect visRect = new Rect();
        ViewGroup parent;
        TagInfo(int idx, @NonNull View view) {
            this.idx = idx;
            view.getGlobalVisibleRect(visRect);
            parent = (ViewGroup)view.getParent();
        }
    }

    private static final int LAYOUT_ID = R.layout.frag_expand_group_demo;

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, LAYOUT_ID);
        setBarVisibility(View.GONE);
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

            if (view.getId() == R.id.page4_tableLayout) {
                viewTouched = findViewAtPosition(tableLayout, x, y);
                if (viewTouched != null) {
                    doAction(viewTouched, tableLayout);
                    return true;
                }
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
        overlay.removeAllViews();
        int checkedRadioButtonId = rg.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.page1_tagRB) {
            restoreGroup(parent);
            if (view.getBackground() == null) {
                // Draw animated gradient of two possible colors.
                view.setBackgroundResource(R.drawable.bg_anim_gradient);
                view.setBackgroundTintList(Math.random() > 0.5 ? colorRed : colorGreen);
                ((AnimatedVectorDrawable) view.getBackground()).start();
            } else {
                view.setBackground(null);
            }
        } else if (checkedRadioButtonId == R.id.page1_grow2RB) {
            expandView(createGroup(parent), parent);
            ((RadioButton) rg.findViewById(R.id.page1_detailsRB)).setChecked(true);
        } else if (checkedRadioButtonId == R.id.page1_detailsRB) {
            if (expander.getChildCount() != 0) {
                openDetailView(expander, parent);
            }
            ((RadioButton) rg.findViewById(R.id.page1_tagRB)).setChecked(true);
        } else if (checkedRadioButtonId == R.id.page1_resetRB) {
            resetUI();
        }
    }

    private final ArrayList<View> groupViews = new ArrayList<>();
    private void restoreGroup(@NonNull ViewGroup parent) {
        for (View child : groupViews) {
            TagInfo tagInfo = (TagInfo)child.getTag(R.id.tag_info);
            if (tagInfo != null) {
                child.setBackground(null);
                child.setLayoutParams( tagInfo.parent.getChildAt(tagInfo.idx).getLayoutParams());
                tagInfo.parent.removeViewAt(tagInfo.idx);
                ((ViewGroup)child.getParent()).removeView(child);
                tagInfo.parent.addView(child, tagInfo.idx);
            }
        }
        groupViews.clear();
        expander.setVisibility(View.INVISIBLE);
    }

    @Nullable
    private ViewGroup createGroup(@NonNull ViewGroup parent) {
        // Collect tagged children.
        restoreGroup(parent);

        addTaggedChildren(parent, groupViews);
        if (groupViews.isEmpty()) {
            return null;
        }
        // Save children visible bounds and union of children bounds.
        Rect bounds = new Rect();
        groupViews.get(0).getGlobalVisibleRect(bounds);
        ArrayList<Rect> rectList = new ArrayList<>();
        for (View child : groupViews) {
            Rect childBnd = new Rect();
            child.getGlobalVisibleRect(childBnd);
            rectList.add(childBnd);
            bounds.union(childBnd);
        }

        expander.removeAllViews();
        for (int idx = 0; idx < groupViews.size(); idx++) {
            View child = groupViews.get(idx);
            Rect childRect = rectList.get(idx);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(childRect.width(), childRect.height());
            params.setMargins(childRect.left - bounds.left, childRect.top - bounds.top,0, 0);

            ViewGroup childParent = (ViewGroup)child.getParent();
            Space space = new Space(getContext());
            space.setLayoutParams(child.getLayoutParams());
            int childIdx = childParent.indexOfChild(child);
            childParent.removeViewAt(childIdx);
            childParent.addView(space, childIdx);

            expander.addView(child, params);
            expander.setTag(R.id.tag_col, child.getTag(R.id.tag_col));
            expander.setTag(R.id.tag_row, child.getTag(R.id.tag_row));
        }

        Rect expanderRect = new Rect();
        ((ViewGroup)tableLayout.getParent()).getGlobalVisibleRect(expanderRect);

        expander.setX(bounds.left - expanderRect.left);
        expander.setY(bounds.top - expanderRect.top);
        ViewGroup.LayoutParams lp =   expander.getLayoutParams();
        lp.width = bounds.width();
        lp.height = bounds.height();
        expander.setLayoutParams(lp);
        expander.setVisibility(View.VISIBLE);

        return expander;
    }



    private void addTaggedChildren(ViewGroup parent, ArrayList<View> childList) {
        for (int idx = 0; idx < parent.getChildCount(); idx++) {
            View child = parent.getChildAt(idx);
            if (child instanceof ViewGroup) {
                addTaggedChildren((ViewGroup)child, childList);
            } else {
                if (child.getBackground() != null) {
                    child.setTag(R.id.tag_info, new TagInfo(idx, child));
                    childList.add(child);
                }
            }
        }
    }

    private void  setClipChildren(ViewGroup view, boolean toClip) {
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

        /*
        int numCol = TestData.WxData.columns();
        int numRow = TestData.WXDATA.length;
        int col = (Integer)view.getTag(R.id.tag_col);
        int row = (Integer)view.getTag(R.id.tag_row);
         */

        ViewGroup.LayoutParams params = view.getLayoutParams();

        // Record layout change and animate it slowly
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.setDuration(ANIM_MILLI);
        transitionSet.addTransition(new AutoTransition());
        transitionSet.addTransition(new Translation());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeBounds());
        TransitionManager.beginDelayedTransition((ViewGroup) rootView, transitionSet);

        // view.setPivotX( (col < numCol/2) ? 0 : view.getWidth());
        // view.setPivotY( (row < numRow/2) ? 0 : view.getHeight());
        view.setPivotX(view.getX() );
        view.setPivotY(view.getY() );

        final float growPercent = 1.21f;
        view.setScaleX(growPercent);
        view.setScaleY(growPercent);

        // Change color and elevation
        view.setBackgroundResource(R.drawable.bg_red);
        view.setElevation(nextElevation);
        nextElevation += 8;

        view.requestLayout();
        view.invalidate();
    }

    private void  openDetailView(View view, ViewGroup parent) {
        // int numCol = getNumCol(parent);
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
        detailTv.setMarker(R.drawable.bg_white_varrow);
        detailTv.setText(TestData.WXDATA[row].getDetails(col));
        detailTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        detailTv.setTextColor(Color.WHITE);

        Drawable icon = AppCompatResources.getDrawable(requireContext(), R.drawable.wx_sun_30d);
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

}
