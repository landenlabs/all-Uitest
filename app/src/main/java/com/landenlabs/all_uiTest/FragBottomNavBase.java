package com.landenlabs.all_uiTest;

import android.app.Activity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
abstract class FragBottomNavBase extends Fragment {

    @IntDef({View.VISIBLE, View.INVISIBLE, View.GONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Visibility {}

    protected ViewGroup root;
    protected androidx.appcompat.widget.Toolbar toolbar;
    protected AppBarLayout appBar;


    @SuppressWarnings("UnusedReturnValue")
    View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, int layoutRes) {
        root = (ViewGroup)inflater.inflate(layoutRes, container, false);
        appBar = container.getRootView().findViewById(R.id.appbar);
        toolbar = container.getRootView().findViewById( R.id.toolbar);
        return root;
    }

    void setBarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            setBarVisibility(View.VISIBLE);
        }
    }

    void setBarVisibility(@Visibility int visibility) {
        if (appBar != null && appBar.getVisibility() != visibility) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(500);
            TransitionManager.beginDelayedTransition((ViewGroup)appBar.getRootView(), autoTransition);
            appBar.setVisibility(visibility);
            appBar.invalidate();
        }
    }

    @NonNull
    Activity getActivitySafe() {
        return Objects.requireNonNull(getActivity());
    }

}
