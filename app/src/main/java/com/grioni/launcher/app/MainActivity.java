package com.grioni.launcher.app;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    public static String INTENT_ACTION_NOTIFICATION_POSTED = "com.grioni.notification_posted";
    public static String INTENT_ACTION_NOTIFICATION_REMOVED = "com.grioni.notification_removed";

    public static final int REQUEST_BIND_APPWIDGET = 9;
    public static final int REQUEST_PICK_APPWIDGET = 5;

    private AppWidgetManager widgetManager;
    private CardAppWidgetHost cardAppWidgetHost;
    private final int CARD_APP_WIDGET_HOST_ID = 34;
    private int nextWidgetId;

    FragmentManager.OnBackStackChangedListener appDrawerStateChanged = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if(!appDrawerOpen) {
                getSupportActionBar().removeAllTabs();
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }

            appDrawerOpen = !appDrawerOpen;
        }
    };

    private ImageView appDrawerOpener;
    private View.OnClickListener onAppDrawerOpen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appDrawerOpen = true;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.app_drawer_container, appDrawer);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    };

    private AppDrawerFragment appDrawer;
    private CardManagerFragment cardManager;

    private boolean appDrawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(appDrawerStateChanged);

        appDrawer = AppDrawerFragment.newInstance();
        appDrawerOpener = (ImageView) findViewById(R.id.app_drawer_open);
        appDrawerOpener.setOnClickListener(onAppDrawerOpen);

        DynamicGridUtils.instantiate(this);
        new AppManager(this);
        widgetManager = AppWidgetManager.getInstance(this);
        cardAppWidgetHost = new CardAppWidgetHost(this, CARD_APP_WIDGET_HOST_ID);

        nextWidgetId = 0;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getResources().getString(R.string.app_tray_enabled), false))
            transaction.add(R.id.app_tray_frame, AppTrayFragment.newInstance());

        cardManager = CardManagerFragment.newInstance(R.layout.fragment_card_manager,
                R.layout.fragment_card_notification);
        transaction.add(R.id.card_manager_frame, cardManager);

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            case R.id.add:
                int appWidgetId =
                        cardAppWidgetHost.allocateAppWidgetId();
                Intent pickIntent =
                        new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                pickIntent.putExtra
                        (AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_BIND_APPWIDGET:
                    if (!widgetManager.bindAppWidgetIdIfAllowed(nextWidgetId,
                            new ComponentName("com.grioni.launcher.app", ".CardAppWidgetProvider"))) {
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, nextWidgetId);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.componentName);

                        startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
                    }

                    break;
            }
        }
    }
}
