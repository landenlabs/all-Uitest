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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Fragment which setups a page with bottom and side navigation.
 */
@SuppressWarnings("FieldCanBeLocal")
public class FragBottomNavMain extends FragBottomNavBase
         implements  View.OnClickListener  {

    private ViewGroup root;

    private final boolean useRadioBottomBar = true;
    private final int LAYOUT_RES = useRadioBottomBar ? R.layout.frag_bottom_rg_nav_main : R.layout.frag_bottom_nav_main;

    public View onCreateView(@NonNull LayoutInflater inflater,
    ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup)inflater.inflate(LAYOUT_RES, container, false);
        // NavController navSideController = Navigation.findNavController(requireActivity(), R.id.sideNavFragment);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (useRadioBottomBar) {
            // Setup custom bottom navigation
            RadioGroup navBar = root.findViewById(R.id.tabHolder);
            PopupMenu popupMenu = new PopupMenu(requireActivity(), root);
            requireActivity().getMenuInflater().inflate(R.menu.menu_bottom, popupMenu.getMenu());
            addTabBar(navBar, popupMenu.getMenu());
        } else {
            // Setup bottom navigation
            // NavGraph bottomNavGraph = navController.getNavInflater().inflate(R.navigation.nav_bottom);
            NavController bottomNavController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment);
            BottomNavigationView bottomNavView = view.findViewById(R.id.bottomNavigation);
            NavigationUI.setupWithNavController(bottomNavView, bottomNavController);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragGridViewDemo:
            case R.id.fragDividerDemo:
            case R.id.fragExpandDemo:
            case R.id.fragExpandGroupViewDemo:
            case R.id.fragExpandGroupImageDemo:
                changePage(view.getId());
                break;

            default:
                Toast.makeText(requireContext(), "Unknown click action ", Toast.LENGTH_LONG).show();
        }
    }

    private void changePage(int id) {
        NavController navBotController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment);
        navBotController.navigate(id);
    }

    // ---------------------------------------------------------------------------------------------
    // Alternate bottom nav bar

    private void addTabBar(@NonNull RadioGroup tabHolder, @NonNull Menu menu) {
        tabHolder.removeAllViews();
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        for (int idx = 0; idx < menu.size(); idx++) {
            RadioButton button = (RadioButton)getLayoutInflater().inflate(R.layout.tab_btn, root, false);
            MenuItem item = menu.getItem(idx);
            Drawable tabBtnIcon = null;
            try {
                tabBtnIcon = item.getIcon();
            } catch (Exception ex) {
                Log.e(TAG, String.format("Failed to load icon for tab# %d, exception=%s",
                        idx, ex.getMessage()));
            }
            if (tabBtnIcon != null) {
                button.setId(item.getItemId());
                button.setText(item.getTitle());
                button.setCompoundDrawableTintList();
                button.setCompoundDrawablesWithIntrinsicBounds(null, tabBtnIcon, null, null);
                button.setOnClickListener(this);
                tabHolder.addView(button, lp);
            }
        }
    }

}
