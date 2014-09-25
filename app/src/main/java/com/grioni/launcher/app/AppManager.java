package com.grioni.launcher.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Matias Grioni on 7/9/14.
 */
public class AppManager extends DataSetObservable {

    private static boolean created = false;
    private static boolean favoritesChanged = false;

    private static List<AppModel> apps = new ArrayList<AppModel>();
    private static List<AppDrawerItem> favorites = new ArrayList<AppDrawerItem>();
    private static List<AppModel> appTray = new ArrayList<AppModel>();

    private FragmentActivity context;
    private int pageLimit;



    /**
     * The created instance of AppManager that is shared. This way the apps are only loaded once.
     */
    private static AppManager instance;

    /**
     *
     */
    private LoaderManager.LoaderCallbacks<List<AppModel>> appLoader = new LoaderManager.LoaderCallbacks<List<AppModel>>() {
        @Override
        public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
            return new AppLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> data) {
            favoritesChanged = false;

            apps = data;
            SharedPreferences appTrayDetails = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> appTrayItems = appTrayDetails.getStringSet("app_tray_items", null);
            List<String> packages = getPackages();

            if(appTrayItems != null) {
                Iterator<String> itr = appTrayItems.iterator();
                while (itr.hasNext()) {
                    int index = packages.indexOf(itr.next());
                    appTray.add(apps.get(index));
                }
            }

            AppModel[] temp = new AppModel[apps.size()];
            FavoritesLoadTask loadTask = new FavoritesLoadTask(context);

            try {
                favorites = loadTask.execute(apps.toArray(temp)).get();
            } catch(InterruptedException ex) {
                    ex.printStackTrace();
            } catch(ExecutionException ex) {
                ex.printStackTrace();
            }

            notifyChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<AppModel>> loader) {
            apps.clear();
            favorites.clear();
            appTray.clear();
        }
    };


    /**
     *
     * @return
     */
    public static AppManager getInstance(int pageLimit) {
        try {
            if (created) {
                instance.setPageLimit(pageLimit);
                return instance;
            }
        } catch(IllegalStateException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @return
     */
    public static AppManager getInstance() {
        try {
            if(created) {
                return instance;
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param activity
     */
    public AppManager(FragmentActivity activity) {
        this(activity, -1);
    }

    /**
     *
     * @param activity
     * @param pageLimit
     */
    public AppManager(FragmentActivity activity, int pageLimit) {
        context = activity;
        this.pageLimit = pageLimit;

        if (!created)
            activity.getSupportLoaderManager().initLoader(0, null, appLoader);
        created = true;

        instance = this;
    }

    /**
     *
     */
    public void forceReload() {
        context.getSupportLoaderManager().restartLoader(0, null, appLoader);
    }

    /**
     *
     * @param listener
     */
    public void addListener(DataSetObserver listener) {
        registerObserver(listener);
    }

    /**
     *
     * @param favorite
     */
    public void addFavorite(AppModel favorite) {
        favoritesChanged = true;

        favorites.add(favorite);
        notifyChanged();
    }

    /**
     *
     * @param position
     */
    public void deleteFavorite(int position) {
        favorites.remove(position);
        notifyChanged();
    }

    /**
     *
     * @param page
     * @param index
     * @return
     */
    public AppModel getApp(int page, int index) {
        return apps.get(page * pageLimit + index);
    }

    /**
     *
     * @param index
     * @return
     */
    public List<AppModel> getPage(int index) {
        if(index >= 0 && index < getPageCount()) {
            int lastIndex = ((index + 1) * pageLimit > apps.size()) ? apps.size() : (index + 1) * pageLimit;

            // Create a new ArrayList to create a deep copy of the list and so that
            // there is no ConcurrentModificationException when modifying adapters
            // that reference these app lists.
            return new ArrayList<AppModel>(apps.subList(index * pageLimit, lastIndex));
        } else
            return null;
    }

    /**
     *
     * @param index
     * @return
     */
    public List<AppDrawerItem> getFavoritePage(int index) {
        if(index >= 0 & index < getFavoritesPageCount()) {
            int lastIndex = ((index + 1) * pageLimit > favorites.size()) ? favorites.size() : (index + 1) * pageLimit;

            // Create a new ArrayList to create a deep copy of the list and so that
            // there is no ConcurrentModificationException when modifying adapters
            // that reference these app lists.
            return new ArrayList<AppDrawerItem>(favorites.subList(index, lastIndex));
        } else
            return null;
    }

    /**
     *
     * @param page
     * @param from
     * @param to
     */
    public void updateFavorites(int page, int from, int to) {
        Collections.swap(favorites, (page * pageLimit) + from, (page * pageLimit) + to);
    }

    /**
     *
     * @return
     */
    public int getPageCount() {
        return (apps.size() / pageLimit) + 1;
    }

    /**
     *
     * @return
     */
    public int getFavoritesPageCount() { return (favorites.size() / pageLimit) + 1;}

    /**
     *
     * @return
     */
    public List<AppDrawerItem> getFavorites() {
        return favorites;
    }

    /**
     *
     * @return
     */
    public List<AppModel> getAppTray() {
        return appTray;
    }

    /**
     *
     * @param pageLimit
     */
    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    /**
     *
     * @return
     */
    public List<AppModel> getApps() {
        return apps;
    }

    /**
     *
     * @param rootPos
     * @param intoPos
     */
    public void addFolder(int rootPos, int intoPos) {
        List<AppModel> members = new ArrayList<AppModel>();
        AppDrawerItem root = favorites.get(rootPos);
        AppDrawerItem into = favorites.get(intoPos);

        if(root instanceof AppModel && into instanceof AppModel) {
            members.add((AppModel) root);
            members.add((AppModel) into);

            AppDrawerFolder newFolder = new AppDrawerFolder(context, members);
            favorites.set(rootPos, newFolder);
            favorites.remove(intoPos);
        } else if(root instanceof AppDrawerFolder && into instanceof AppModel) {
            ((AppDrawerFolder) root).addMemberApp((AppModel) into);
            favorites.remove(intoPos);
        } else if(into instanceof AppDrawerFolder && root instanceof AppModel) {
            ((AppDrawerFolder) into).addMemberApp((AppModel) root);

            favorites.set(rootPos, into);
            favorites.remove(intoPos);
        } else {
            List<AppModel> others = ((AppDrawerFolder) into).getMemberApps();
            ((AppDrawerFolder) root).addMemberApps(others);

            favorites.remove(intoPos);
        }

        notifyChanged();
    }

    /**
     *
      * @return
     */
    public boolean areFavoritesChanged() {
        return favoritesChanged;
    }

    /**
     *
     * @param mFavoritesChanged
     */
    public void setFavoritesChanged(boolean mFavoritesChanged) {
        favoritesChanged = mFavoritesChanged;
    }

    /**
     *
     * @return
     */
    private List<String> getPackages() {
        List<String> packages = new ArrayList<String>(apps.size());

        for(int i = 0; i < apps.size(); i++) {
            packages.add(apps.get(i).getPackageName());
        }

        return packages;
    }
}
