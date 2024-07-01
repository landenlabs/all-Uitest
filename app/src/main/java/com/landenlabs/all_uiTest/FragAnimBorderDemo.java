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

import android.annotation.SuppressLint;
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
 * <p>
 * Sample animated heart pulse.
 * https://github.com/IhorKlimov/Android-Animations/blob/master/app/src/main/res/drawable/heart_rate.xml
 * <p>
 * Online tool to adjust AVD
 * https://shapeshifter.design/
 * <p>
 * https://github.com/harjot-oberai/VectorMaster
 */
@SuppressWarnings("FieldCanBeLocal")
public class FragAnimBorderDemo extends FragBottomNavBase
        implements View.OnTouchListener {

    private TableLayout tableLayout;
    private ImageView avecIV;
    private static final int LAYOUT_ID = R.layout.frag_anim_border;

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, LAYOUT_ID);
        setBarTitle("Animate Border");
        initUI();

        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
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

    @SuppressLint("ClickableViewAccessibility")
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

            if (view.getId() == R.id.page6_tableLayout) {
                viewTouched = findViewAtPosition(tableLayout, globalXpx, globalYpx);
                if (viewTouched != null) {
                    doAction(viewTouched, tableLayout);
                    return true;
                }
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
    @SuppressWarnings("unused")
    private void doAction(View view, ViewGroup parent) {
    }

}
