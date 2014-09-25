package com.grioni.launcher.app;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.WeakHashMap;

/**
 * A ViewPager adapter for DynamicGridView which is a page or tab in the ViewPager. Handles the
 * creation of each of these pages (DynamicGridViews), with provided xml resources, and listeners,
 * and data.
 *
 * Created by Matias Grioni on 6/19/14.
 */
public class AppTabAdapter extends FragmentPagerAdapter {
    private AppManager manager;

    private int appGridId;
    private int favGridId;
    private int gridItemResourceId;

    private WeakHashMap<Integer, Fragment> registeredFragments;

    private OnAppFavoritedListener userAppFavorited;
    private OnAppFavoritedListener localAppFavorited = new OnAppFavoritedListener() {
        @Override
        public void onAppFavorited(AppTransferInfo favoriteInfo) {
            if(userAppFavorited != null)
                userAppFavorited.onAppFavorited(favoriteInfo);
        }
    };

    /**
     * Use this constructor for when the ViewPager is within a Fragment.
     * @param fragment
     * @param appGridId - The xml resource to inflate the entire DynamicGridView.
     * @param favGridId
     * @param gridItemResourceId - The xml resource to inflate each individual item.
     */
    public AppTabAdapter(Fragment fragment, int appGridId, int favGridId, int gridItemResourceId, int pageLimit) {
        super(fragment.getChildFragmentManager());

        manager = AppManager.getInstance(pageLimit);

        this.appGridId = appGridId;
        this.favGridId = favGridId;
        this.gridItemResourceId = gridItemResourceId;

        registeredFragments = new WeakHashMap<Integer, Fragment>();
    }

    /**
     *
     * @return
     */
    @Override
    public int getCount() {
        return (manager.getPageCount() + manager.getFavoritesPageCount());
    }

    @Override
    public Fragment getItem(int position) {
        DrawerPageFragment appPage;

        if(position < manager.getFavoritesPageCount())
            appPage = FavoritePageFragment.newInstance(position, favGridId, gridItemResourceId);
        else {
            appPage = AppPageFragment.newInstance(position - manager.getFavoritesPageCount(),
                    appGridId, gridItemResourceId);
            ((AppPageFragment) appPage).setAppFavoritedListener(localAppFavorited);
        }

        registeredFragments.put(position, appPage);

        return appPage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        registeredFragments.remove(position);
    }

    /**
     *
     * @param position
     * @return
     */
    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }

    /**
     *
     * @param onAppFavorited
     */
    public void setAppFavoritedListener(OnAppFavoritedListener onAppFavorited) {
        this.userAppFavorited = onAppFavorited;
    }
}
