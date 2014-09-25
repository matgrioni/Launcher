package com.grioni.launcher.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Matias Grioni on 8/23/14.
 */
public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new SettingsFragment()).commit();
    }
}
