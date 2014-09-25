package com.grioni.launcher.app;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import java.util.List;

/**
 * Created by Matias Grioni on 8/23/14.
 */
public class SettingsFragment extends PreferenceFragment {
    AppManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = AppManager.getInstance();

        addPreferencesFromResource(R.xml.preferences);
        MultiSelectListPreference appTrayPref
                = (MultiSelectListPreference) findPreference(getResources().getString(R.string.app_tray_items));

        // Set the names for the
        List<AppModel> apps = manager.getApps();
        CharSequence[] appNames = new CharSequence[apps.size()];
        CharSequence[] packageNames = new CharSequence[apps.size()];

        for(int i = 0; i < appNames.length; i++) {
            appNames[i] = apps.get(i).getLabel();
            packageNames[i] = apps.get(i).getPackageName();
        }

        appTrayPref.setEntries(appNames);
        appTrayPref.setEntryValues(packageNames);
    }
}
