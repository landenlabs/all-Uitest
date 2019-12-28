package utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.Nullable;


/**
 * https://github.com/andkulikov/Transitions-Everywhere
 * Static utility methods for Transitions.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class TransitionUtils {

    @Nullable
    static Animator mergeAnimators(@Nullable Animator animator1, @Nullable Animator animator2) {
        if (animator1 == null) {
            return animator2;
        } else if (animator2 == null) {
            return animator1;
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, animator2);
            return animatorSet;
        }
    }

}