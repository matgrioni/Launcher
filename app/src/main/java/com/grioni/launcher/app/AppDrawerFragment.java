package com.grioni.launcher.app;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Fragment which displays the list of installed apps through a ViewPager and ActionBar tabs.
 *
 * Created by Matias Grioni on 5/14/14.
 */
public class AppDrawerFragment extends Fragment {

    private ActionBar actionBar;
    private AppViewPager appPager;
    private AppTabAdapter pagesAdapter;

    private ActionBar.Tab favoritesTab;
    private ActionBar.Tab appsTab;

    private int pageLimit;

    /**
     * When an app is favorited, go the first page so that the user can choose its placement.
     */
    private OnAppFavoritedListener onAppFavorited = new OnAppFavoritedListener() {
        @Override
        public void onAppFavorited(int position) {
            appPager.setCurrentItem(0);
        }
    };

    /**
     * When the page is changed on the view pager, change which tab is selected.
     */
    ViewPager.SimpleOnPageChangeListener pageChanged = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // Since there are 2 tabs, one for favorites, and the other for multiple pages of apps:
            // if the position is 0 then it is the favorites tab, otherwise it's one of the app pages.
            if(position == 0)
                actionBar.setSelectedNavigationItem(position);
            else if(position > 0)
                actionBar.setSelectedNavigationItem(1);
        }
    };

    /**
     * Changes the current app page selected based on the selected tab.
     */
    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // Since the first tab is selected before the app pager has been initialized check for
            // this condition.
            if(appPager != null)
                appPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // Nothing to do
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // Nothing to do
        }
    };

    /**
     * @return A new instance of an AppDrawerFragment, with the amount of columns and rows per page
     * calculated.
     */
    public static AppDrawerFragment newInstance() {
        return new AppDrawerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Setup the action bar tabs and up navigation every time the AppDrawer is launched.
        actionBar = ((ActionBarActivity) activity).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        favoritesTab = actionBar.newTab().setText("Favorites").setTabListener(tabListener);
        appsTab = actionBar.newTab().setText("Apps").setTabListener(tabListener);

        actionBar.addTab(favoritesTab);
        actionBar.addTab(appsTab);

        actionBar.setDisplayHomeAsUpEnabled(true);

        calcPageLimit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Create the ViewPager adapter with the layout parameters for each different app page type.
        appPager = (AppViewPager) inflater.inflate(R.layout.fragment_app_drawer, container, false);
        pagesAdapter = new AppTabAdapter(this, R.layout.app_page_gridview, R.layout.favorite_page_gridview,
                R.layout.griditem_app, pageLimit);
        pagesAdapter.setAppFavoritedListener(onAppFavorited);

        appPager.setOnPageChangeListener(pageChanged);
        appPager.setAdapter(pagesAdapter);

        return appPager;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        actionBar.setDisplayHomeAsUpEnabled(false);

        // All of this that follows is a bug fix for the Android system.
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculate how many items the page can have based on the height of the icons and the padding.
     * TODO: This still needs to be changed so that it doesn't overflow.
     */
    private void calcPageLimit() {
        int iconSize = (int) getResources().getDimension(R.dimen.app_icon_size);
        int padding = (int) getResources().getDimension(R.dimen.app_vertical_margin);

        // Later would be replaced with something other than 4
        pageLimit = DynamicGridUtils.getRows(iconSize, padding) * 4;
    }

    /**
     *
     */
    public void update() {
        if(appPager != null) {
            // Update app tab adapter if the fragment has been created so far, otherwise there is no
            // need to update because the fragment's view has not been created.
            pagesAdapter = new AppTabAdapter(this, R.layout.app_page_gridview, R.layout.favorite_page_gridview,
                    R.layout.griditem_app, pageLimit);
            appPager.setAdapter(pagesAdapter);
        }
    }
}
