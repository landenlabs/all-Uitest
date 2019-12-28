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

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main activity for All-UiTest which demonstrates cell expansion and dividers.
 */
@SuppressWarnings("Convert2Lambda")
public class MainActivity extends AppCompatActivity {

    static final String TAG = "UiTest";

    androidx.appcompat.widget.Toolbar toolbar;
    NavController navSideController;
    NavController navBotController;
    String intentAction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navSideController = Navigation.findNavController(this, R.id.sideNavFragment);

        // Set up ActionBar
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navSideController, drawerLayout);

        // Set up side navigation draw menu
        NavigationView navSideView = findViewById(R.id.navSideView);
        NavigationMenuView navMenuView = (NavigationMenuView) navSideView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        NavigationUI.setupWithNavController(navSideView, navSideController);

        // Setup shortcuts for Bottom Nav items.
        navSideView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup bottomNavigationView = findViewById(R.id.bottomNavigation);
                if (bottomNavigationView != null) {
                    addOrExecuteShortcut(bottomNavigationView);
                }
            }
        });

        intentAction = getIntent() != null ? getIntent().getAction() : null;
        Log.d(TAG, String.format(Locale.US, "indent action=%s", intentAction));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate  menu, adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_side, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        return NavigationUI.navigateUp(
                Navigation.findNavController(this, R.id.sideNavFragment), drawerLayout);
    }

    /**
     * Add or execute short cut
     */
    @SuppressWarnings("ConstantConditions")
    private void addOrExecuteShortcut(@NonNull ViewGroup bottomNavGroup) {
        BottomNavigationView bottomNavigationView;
        if (bottomNavGroup instanceof BottomNavigationView) {
            bottomNavigationView = (BottomNavigationView)bottomNavGroup;
        } else {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT <android.os.Build.VERSION_CODES.N_MR1) {
            return;
        }

        int menuSize = bottomNavigationView.getMenu().size();
        Map<String, MenuItem> menus = new HashMap<>(menuSize);
        for (int menuIdx = 0; menuIdx < menuSize; menuIdx++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(menuIdx);
            menus.put(menuItem.getTitle().toString(), menuItem);
        }

        ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
        navBotController = Navigation.findNavController(this, R.id.bottomNavFragment);
        Iterator<NavDestination> navIT =  navBotController.getGraph().iterator();
        List<ShortcutInfo> shortcutList = new ArrayList<>();
        while (navIT.hasNext()) {
            NavDestination navDestination = navIT.next();
            MenuItem menuItem = menus.get(navDestination.getLabel());
            if (menuItem != null) {
                Intent newTaskIntent = new Intent(this, MainActivity.class);
                newTaskIntent.setAction(navDestination.getLabel().toString());
                newTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                ShortcutInfo postShortcut
                        = new ShortcutInfo.Builder(this, navDestination.getLabel().toString())
                        .setShortLabel(navDestination.getLabel())
                        .setLongLabel(navDestination.getLabel())
                        .setIcon(Icon.createWithResource(this, R.drawable.tab_gridview))
                        .setIntent(newTaskIntent)
                        .build();
                shortcutList.add(postShortcut);

                if (navDestination.getLabel().equals(intentAction)) {
                    // Execute shortcut
                    navBotController.navigate(navDestination.getId());
                }
            }
        }

        shortcutManager.addDynamicShortcuts(shortcutList);
    }
}
