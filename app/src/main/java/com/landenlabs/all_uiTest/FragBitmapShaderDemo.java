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

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import utils.SeekBarExt1;
import utils.TextViewExt1;

/**
 * Fragment demonstrates how BitmapShader works, shadows and blur.
 */
public class FragBitmapShaderDemo extends FragBottomNavBase
        implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private RadioGroup rg;
    private TextViewExt1 shaderTv;
    private SeekBarExt1 slider;
    private static final int LAYOUT_ID = R.layout.frag_bitmapshader;
    private MediaPlayer soundClick;
    private SeekBarExt1 elevSb, shadowSb, widthSb, heightSb;
    private View controlGrid ;
    private ImageView expandBtn;
    private float elevF = 0f;
    private int shadowPx = 20;
    private int widthPx = 200;
    private int heightPx = 200;

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
        rg.setOnCheckedChangeListener((group, checkedId) -> doAction());

        slider = root.findViewById(R.id.page7_slider);
        slider.setOnSeekBarChangeListener(this);

        expandBtn =  root.findViewById(R.id.page7_expand_btn);
        expandBtn.setOnClickListener(this);

        controlGrid = root.findViewById(R.id.page7_control_grid);

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

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            soundClick.start();
            doAction();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.page7_expand_btn) {
            controlGrid.setVisibility(controlGrid.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            expandBtn.setImageResource(
                    controlGrid.getVisibility() == View.VISIBLE ? R.drawable.vec_expand_close :
                            R.drawable.vec_expand_open);
        }
    }

    private void doAction() {

        // Change TextViewExt1.
        //   Try to just reset drawables, but the dimensions and padding get corrupted.
        //   So remake TextViewExt1
        boolean shader = false;
        int checkedRadioButtonId = rg.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.page7_9patch) {
            // shaderTv.setMarker(-1);
            // shaderTv.setBackgroundResource(R.drawable.bg_white_varrow9);
            // shaderTv.init();
        } else if (checkedRadioButtonId == R.id.page7_shader) {
            shader = true;
            // shaderTv.setMarker(R.drawable.bg_white_varrow);
            // shaderTv.setBackground(null);
            // shaderTv.init();
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
        textViewExt1.setMaskerColor( 0xff4080f0);
        if (shader) {
            // Set shader
            textViewExt1.setMarker(R.drawable.bg_white_varrow);
        } else {
            // Set 9-patch
            textViewExt1.setBackgroundResource(R.drawable.bg_white_varrow9);
        }
        textViewExt1.setText(R.string.page7_text_msg);
        textViewExt1.setTextColor(requireContext().getColor(R.color.text_light));

        Drawable icon = AppCompatResources.getDrawable(requireContext(), R.drawable.wx_sun_30d);
        textViewExt1.setForeground(icon);
        textViewExt1.setForegroundGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        int padPx = requireContext().getResources().getDimensionPixelSize(R.dimen.page7_pad_side);
        textViewExt1.setPadding(padPx, padPx*2, padPx, padPx*4);
        textViewExt1.setClipBottomPx(padPx*2);

        ViewGroup.LayoutParams layoutParams =  oldTextViewExt.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                // requireContext().getResources().getDimensionPixelSize(R.dimen.page7_shader_width);
        textViewExt1.setLayoutParams(layoutParams);

        return textViewExt1;
    }

    void setValue(float percent) {
        shaderTv.setPointer((percent - 0.5f) * shaderTv.getWidth());    // TODO - why is this not width/2

    }

    /**
     * Execute action on touched view.
     */
    private void doAction(View view, ViewGroup parent) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float percent = progress / 100.0f;
        int id = seekBar.getId();
        if (id == R.id.page7_slider) {
            setValue(percent);
        } else if (id == R.id.page7_elev_sb) {
            elevF = (percent - 0.5f) * 50;
            shaderTv.setElevation(elevF);
        } else if (id == R.id.page7_shadow_sb) {
            shadowPx = Math.round(percent * 100);
            shaderTv.setShadowSizePx(shadowPx);
        } else if (id == R.id.page7_width_sb) {
            widthPx = Math.round(percent * 1000);
            shaderTv.setWidth(widthPx);
        } else if (id == R.id.page7_height_sb) {
            heightPx = Math.round(percent * 1000);
            shaderTv.setHeight(heightPx);
        }


        shaderTv.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
