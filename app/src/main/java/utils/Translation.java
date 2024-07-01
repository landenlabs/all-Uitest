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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

/**
 * https://github.com/andkulikov/Transitions-Everywhere
 * This transition tracks changes to the translationX and translationY of
 * target views in the start and end scenes and animates change.
 * <p/>
 * Created by Andrey Kulikov on 13/03/16.
 */
public class Translation extends Transition {

    private static final String TRANSLATION_X = "Translation:translationX";
    private static final String TRANSLATION_Y = "Translation:translationY";
    private static final String[] sTransitionProperties = {
            TRANSLATION_X,
            TRANSLATION_Y
    };

    @Nullable
    private static final Property<View, PointF> TRANSLATION_PROPERTY;

    static {
        TRANSLATION_PROPERTY = new Property<View, PointF>(PointF.class, "translation") {

            @Override
            public void set(@NonNull View object, @NonNull PointF value) {
                object.setTranslationX(value.x);
                object.setTranslationY(value.y);
            }

            @Override
            public PointF get(@NonNull View object) {
                return new PointF(object.getTranslationX(), object.getTranslationY());
            }
        };
    }

    public Translation() {
    }

    public Translation(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    @Nullable
    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(@NonNull TransitionValues transitionValues) {
        transitionValues.values.put(TRANSLATION_X, transitionValues.view.getTranslationX());
        transitionValues.values.put(TRANSLATION_Y, transitionValues.view.getTranslationY());
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Nullable
    @Override
    public Animator createAnimator(
            @NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues,
            @Nullable TransitionValues endValues) {
        if (startValues != null && endValues != null) {
            float startX = (float) startValues.values.get(TRANSLATION_X);
            float startY = (float) startValues.values.get(TRANSLATION_Y);
            float endX = (float) endValues.values.get(TRANSLATION_X);
            float endY = (float) endValues.values.get(TRANSLATION_Y);
            endValues.view.setTranslationX(startX);
            endValues.view.setTranslationY(startY);

            if (TRANSLATION_PROPERTY != null) {
                Path path = getPathMotion().getPath(startX, startY, endX, endY);
                return ObjectAnimator.ofObject(endValues.view, TRANSLATION_PROPERTY, null, path);
            } else {
                Animator x = (startX == endX) ? null :
                        ObjectAnimator.ofFloat(endValues.view, View.TRANSLATION_X, startX, endX);
                Animator y = (startY == endY) ? null :
                        ObjectAnimator.ofFloat(endValues.view, View.TRANSLATION_Y, startY, endY);
                return TransitionUtils.mergeAnimators(x, y);
            }
        } else {
            return null;
        }
    }

}