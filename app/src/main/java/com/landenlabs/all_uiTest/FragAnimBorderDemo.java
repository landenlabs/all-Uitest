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
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;

/**
 * Fragment demonstrates animating a path around the container cells.
 *
 * Sample animated heart pulse.
 * https://github.com/IhorKlimov/Android-Animations/blob/master/app/src/main/res/drawable/heart_rate.xml
 *
 * Online tool to adjust AVD
 * https://shapeshifter.design/
 *
 * https://github.com/harjot-oberai/VectorMaster
 *
 */
public class FragAnimBorderDemo extends FragBottomNavBase
        implements View.OnTouchListener {

    private TableLayout tableLayout;
    private ImageView avecIV;

    private ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{Color.GREEN }
    );

    private static final int LAYOUT_ID = R.layout.frag_anim_border;

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, LAYOUT_ID);
        setBarTitle("Animate Border");
        initUI();

        return root;
    }

    private void initUI() {
        tableLayout = root.findViewById(R.id.page6_tableLayout);
        avecIV = root.findViewById(R.id.page6_avec_image);
        if (avecIV.getDrawable() instanceof AnimatedVectorDrawable) {
            final AnimatedVectorDrawable avec = (AnimatedVectorDrawable)avecIV.getDrawable();
            avec.start();
            avec.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    avec.start();

                }
            });
        }

        tableLayout.setOnTouchListener(this);
    }
    private void resetUI() {
        ViewGroup parent = (ViewGroup)root.getParent();
        parent.removeAllViews();
        root = (ViewGroup) View.inflate(getContext(), LAYOUT_ID, parent);
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
                case R.id.page6_tableLayout:
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
    }

}
