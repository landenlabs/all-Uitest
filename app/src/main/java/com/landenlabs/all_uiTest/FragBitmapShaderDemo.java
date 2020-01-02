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

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import utils.SeekBarExt1;
import utils.TextViewExt1;

/**
 * Fragment demonstrates how BitmapShader works, shadows and blur.
 */
public class FragBitmapShaderDemo extends FragBottomNavBase
        implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private RadioGroup rg;
    private TextViewExt1 shaderTv;
    private SeekBarExt1 slider;
    private static final int LAYOUT_ID = R.layout.frag_bitmapshader;
    private MediaPlayer soundClick;
    private SeekBarExt1 elevSb, shadowSb, widthSb, heightSb;

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, LAYOUT_ID);
        setBarTitle("BitmapShader, Shadow, Blur");
        soundClick = MediaPlayer.create(requireContext(), R.raw.click);
        initUI();

        return root;
    }

    private void initUI() {
        root.setOnTouchListener(this);
        shaderTv = root.findViewById(R.id.page7_shaderTv);

        rg = root.findViewById(R.id.page7_rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                doAction();
            }
        });

        slider = root.findViewById(R.id.page7_slider);
        slider.setOnSeekBarChangeListener(this);

        elevSb = root.findViewById(R.id.page7_elev_sb);
        elevSb.setOnSeekBarChangeListener(this);

        shadowSb = root.findViewById(R.id.page7_shadow_sb);
        shadowSb.setOnSeekBarChangeListener(this);

        widthSb = root.findViewById(R.id.page7_width_sb);
        widthSb.setOnSeekBarChangeListener(this);

        heightSb = root.findViewById(R.id.page7_height_sb);
        heightSb.setOnSeekBarChangeListener(this);
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
            soundClick.start();
            doAction();
        }
        return false;
    }

    void doAction() {

        // Change TextViewExt1.
        //   Try to just reset drawables, but the dimensions and padding get corrupted.
        //   So remake TextViewExt1
        boolean shader = false;
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.page7_9patch:

                shader = false;
                // shaderTv.setMarker(-1);
                // shaderTv.setBackgroundResource(R.drawable.bg_white_varrow9);
                // shaderTv.init();
                break;
            case R.id.page7_shader:
                shader = true;
                // shaderTv.setMarker(R.drawable.bg_white_varrow);
                // shaderTv.setBackground(null);
                // shaderTv.init();
                break;
        }

        TextViewExt1 newTextViewExt = makeTextViewExt(shaderTv, shader);
        shaderTv = swapView(shaderTv, newTextViewExt);
    }

    /**
     * Swap child views by reparenting them.
     */
    private static <T extends View> T swapView(T oldView, T newView) {
        newView.setLayoutParams(oldView.getLayoutParams());
        ViewGroup parent = (ViewGroup)oldView.getParent();
        int idx = parent.indexOfChild(oldView);
        parent.removeView(oldView);
        parent.addView(newView, idx, newView.getLayoutParams());
        return newView;
    }

    /*
        android:paddingStart="20dp"
        android:paddingEnd="20dp"

        android:id="@+id/page7_shaderTv"
        android:layout_gravity="center_horizontal"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:textColor="@color/text_light"
        android:text="@string/page7_text_msg"

        app:clipBottom="40dp"
        app:shadowRadius="6dp"
        android:paddingBottom="75dp"
        android:foreground="@drawable/wx_sun_32d"
        android:foregroundGravity="bottom|center_horizontal"

        android:paddingTop="40dp"
        app:markerColor="#c48f"
        app:marker="@drawable/bg_white_varrow"
     */
    private TextViewExt1 makeTextViewExt(TextViewExt1 oldTextViewExt, boolean shader) {
        TextViewExt1 textViewExt1 = new TextViewExt1(requireContext());
        if (shader) {
            // Set shader
            textViewExt1.setMarker(R.drawable.bg_white_varrow);
        } else {
            // Set 9-patch
            textViewExt1.setBackgroundResource(R.drawable.bg_white_varrow9);
        }
        textViewExt1.setText(R.string.page7_text_msg);
        textViewExt1.setTextColor(requireContext().getResources().getColor(R.color.text_light));

        Drawable icon = textViewExt1.getContext().getDrawable(R.drawable.wx_sun_30d);
        textViewExt1.setForeground(icon);
        textViewExt1.setForegroundGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        int padPx = requireContext().getResources().getDimensionPixelSize(R.dimen.page7_pad_side);
        textViewExt1.setPadding(padPx, padPx*2, padPx, padPx*4);
        textViewExt1.setClipBottomPx(padPx*2);

        ViewGroup.LayoutParams layoutParams =  oldTextViewExt.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = requireContext().getResources().getDimensionPixelSize(R.dimen.page7_shader_width);
        textViewExt1.setLayoutParams(layoutParams);

        return textViewExt1;
    }

    void setValue(float percent) {
        shaderTv.setPointer((percent - 0.5f) * shaderTv.getWidth());    // TODO - why is this not width/2
        shaderTv.invalidate();
    }

    /**
     * Execute action on touched view.
     */
    private void doAction(View view, ViewGroup parent) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.page7_slider:
                setValue(progress / 100.0f);
                break;
            case R.id.page7_elev_sb:
                break;
            case R.id.page7_shadow_sb:
                break;
            case R.id.page7_width_sb:
                break;
            case R.id.page7_height_sb:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
