package com.grioni.launcher.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class that asynchronously loads the list of installed apps.
 *
 * Created by Matias Grioni on 5/11/14.
 */
public class AppLoader extends AsyncTaskLoader<List<AppModel>> {
    public static final String APP_FAVORITES_CONFIG_FILE = "app_favorites_config";

    private List<AppModel> apps;
    private PackageManager pmanager;
    private PackageIntentReceiver pobserver;

    /**
     * Compare two AppModel objects based on their label or title (alphabetically).
     */
    private static final Comparator<AppModel> appAlphabetical = new Comparator<AppModel>() {
        Collator collator = Collator.getInstance();

        @Override
        public int compare(AppModel lhs, AppModel rhs) {
            return collator.compare(lhs.getLabel(), rhs.getLabel());
        }
    };

    /**
     *
     * @param context
     */
    public AppLoader(Context context) {
        super(context);

        // Get the package manager using getContext, rather than simply context, in order to get the
        // global application context returned by it.
        pmanager = getContext().getPackageManager();
    }

    /**
     * Creates a new set of data, the installed applications to show in an application list, to be
     * published by the loader.
     *
     * @return The data set of installed applications to be published by the loader.
     */
    @Override
    public List<AppModel> loadInBackground() {
        // Get all known applications.
        List<ApplicationInfo> appInfos =  pmanager.getInstalledApplications(0);

        if(appInfos == null)
            appInfos = new ArrayList<ApplicationInfo>();

        // For each application represented by an ApplicationInfo object, create an AppModel object.
        final Context context = getContext();
        List<AppModel> appModels = new ArrayList<AppModel>(appInfos.size());
        for(int i = 0; i < appInfos.size(); i++) {
            ApplicationInfo appInfo = appInfos.get(i);
            String packageName = appInfo.packageName;

            // If the package for this application returns an intent that is not null then it is an
            // installed app and add it to the list of AppModels to return.
            if(context.getPackageManager().getLaunchIntentForPackage(packageName) != null) {
                AppModel app = new AppModel(this, appInfo);
                appModels.add(app);
            }
        }

        Collections.sort(appModels, appAlphabetical);

        return appModels;
    }

    /**
     * Delivers the result of loading the data to the client. In other words, delivers the most up
     * to date data to the client and releases and necessary resources.
     *
     * @param appModels The data or list of applications to deliver to the client.
     */
    @Override
    public void deliverResult(List<AppModel> appModels) {
        // If the loader is stopped / reset then the result here is not needed and release its
        // resources if possible.
        if(isReset())
            if(appModels != null)
                onReleaseResources(appModels);

        // Rearrange these variables in this way to update the member variable list of apps, and to
        // be able to release the resources associated with the old application list properly.
        List<AppModel> oldAppModels = apps;
        apps = appModels;

        // If the loader is already started then just deliver the data to the client.
        if(isStarted())
            super.deliverResult(appModels);

        // Release the resources associated with oldAppModels if possible.
        if(oldAppModels != null)
            onReleaseResources(oldAppModels);
    }

    /**
     * Called when the loader is requested to start, and will load the info if needed or simply
     * deliver the result if possible.
     */
    @Override
    protected void onStartLoading() {
        // If we already have a result available when the client wants to start loading, deliver it
        // immediately.
        if(apps != null)
            deliverResult(apps);

        // If the package observer is null to start with, then initialize it.
        if(pobserver == null)
            pobserver = new PackageIntentReceiver(this);

        // If the data has changed since the last time it was loaded or the apps list is null because
        // nothing has been loaded yet, then load the apps.
        if(takeContentChanged() || apps == null)
            forceLoad();
    }

    /**
     * Cancel the current load if possible.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Called when the loader is requested to cancel the load.
     * @param appModels
     */
    @Override
    public void onCanceled(List<AppModel> appModels) {
        super.onCanceled(appModels);

        // Release the resources of the loaded apps as they are no longer needed since the request
        // was canceled.
        onReleaseResources(appModels);
    }

    /**
     * Called when the loader is requested to reset.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure that the loader is stopped.
        onStopLoading();

        // If apps is not null and has a value loaded into it, then release its resources, and reset
        // apps to null.
        if(apps != null) {
            onReleaseResources(apps);
            apps = null;
        }

        // If the package observer is not null then stop listening for changes to packages by
        // unregistering it as a receiver from the context and setting it to null.
        if(pobserver != null) {
            getContext().unregisterReceiver(pobserver);
            pobserver = null;
        }
    }

    /**
     * Release any resources associated with the list of applications.
     * @param appModels
     */
    protected void onReleaseResources(List<AppModel> appModels) {

    }

    /**
     *
     * @return
     */
    public PackageManager getPackageManager() {
        return pmanager;
    }
}
