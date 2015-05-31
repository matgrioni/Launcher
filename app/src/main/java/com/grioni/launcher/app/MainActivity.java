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

    /**
     * When the back stack is changed, if the app drawer was previously opened, then the action bar
     * tabs are still present. When the app drawer is closed the tabs for the app drawer are
     * explicitly removed since otherwise they will still be there on the main launcher page.
     */
    FragmentManager.OnBackStackChangedListener appDrawerStateChanged =
            new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if(!appDrawer.isVisible()) {
                getSupportActionBar().removeAllTabs();
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        }
    };

    /**
     * The ImageView and the listener that opens the app drawer when this view is clicked.
     */
    private ImageView appDrawerOpener;
    private View.OnClickListener onAppDrawerOpen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Display the app drawer to the screen and add it to the back stack so users can exit
            // it.
            DynamicGridUtils.instantiate(MainActivity.this);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.app_drawer_container, appDrawer);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    };

    private AppDrawerFragment appDrawer;
    private CardManagerFragment cardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(appDrawerStateChanged);

        AppManager.instantiate(this);

        appDrawer = AppDrawerFragment.newInstance();
        appDrawerOpener = (ImageView) findViewById(R.id.app_drawer_open);
        appDrawerOpener.setOnClickListener(onAppDrawerOpen);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Load the preferences (ie. is it enabled and how many items to display)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getResources().getString(R.string.app_tray_enabled), false))
            transaction.add(R.id.app_tray_frame, AppTrayFragment.newInstance());

        // Create the card manager fragment to handle all the different app cards seen on main UI
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

            // When there has been a fragment added over the MainActivity, navigate back when the
            // back button in the action bar is pressed.
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }*/
}
