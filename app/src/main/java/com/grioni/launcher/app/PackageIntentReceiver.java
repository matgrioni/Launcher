package com.grioni.launcher.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Matias Grioni on 5/11/14.
 */
public class PackageIntentReceiver extends BroadcastReceiver {
    private final AppLoader loader;

    public PackageIntentReceiver(AppLoader loader) {
        this.loader = loader;

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");

        loader.getContext().registerReceiver(this, filter);

        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);

        loader.getContext().registerReceiver(this, filter);
    }

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        loader.onContentChanged();
    }
}
