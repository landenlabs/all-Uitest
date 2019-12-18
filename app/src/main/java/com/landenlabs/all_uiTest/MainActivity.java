package com.landenlabs.all_uiTest;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.mainNavFragment);

        // Set up ActionBar
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        // Set up navigation menu
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.post(new Runnable() {
            @Override
            public void run() {
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
                addShortcut(bottomNavigationView);
            }
        });


        String action = getIntent() != null ? getIntent().getAction() : null;
        if (action1.equals(action)) {
            // Show the correct fragment
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_side, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        return NavigationUI.navigateUp(
                Navigation.findNavController(this, R.id.mainNavFragment), drawerLayout);
    }

    private static final String action1= "action1";
    private void addShortcut( BottomNavigationView bottomNavigationView) {
        if (android.os.Build.VERSION.SDK_INT <android.os.Build.VERSION_CODES.N_MR1)
            return;

        int menuSize = bottomNavigationView.getMenu().size();
        Map<String, MenuItem> menus = new HashMap<>(menuSize);
        for (int menuIdx = 0; menuIdx < menuSize; menuIdx++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(menuIdx);
            menus.put(menuItem.getTitle().toString(), menuItem);
        }

        ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
        Iterator<NavDestination> navIT =  navController.getGraph().iterator();
        List<ShortcutInfo> shortcutList = new ArrayList<>();
        while (navIT.hasNext()) {
            NavDestination navDestination = navIT.next();
            MenuItem menuItem = menus.get(navDestination.getLabel());
            if (menuItem != null) {
                Intent newTaskIntent = new Intent(this, MainActivity.class);
                newTaskIntent.setAction(navDestination.getNavigatorName());
                newTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                ShortcutInfo postShortcut
                        = new ShortcutInfo.Builder(this, navDestination.getLabel().toString())
                        .setShortLabel(navDestination.getLabel())
                        .setLongLabel(navDestination.getLabel())
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_android_black_24dp))
                        .setIntent(newTaskIntent)
                        .build();
                shortcutList.add(postShortcut);
            }
        }

        shortcutManager.addDynamicShortcuts(shortcutList);
    }
}
