package com.grioni.launcher.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

/**
 * Created by Matias Grioni on 7/8/14.
 */
public class AppPageFragment extends DrawerPageFragment {

    private OnDeleteItemListener onAppUninstalled = new OnDeleteItemListener() {
        @Override
        public void onItemDeleted(int index) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + manager.getApp(position, index)));
            startActivity(intent);
        }
    };

    private OnAppFavoritedListener userOnAppFavorited;
    private OnAppFavoritedListener localOnAppFavorited = new OnAppFavoritedListener() {
        @Override
        public void onAppFavorited(int innerIndex) {
            // Add the favorited app to the favorites list by getting the app from the app manager
            // based on the page index and app index in the page.
            manager.addFavorite(manager.getApp(position, innerIndex));

            if(userOnAppFavorited != null)
                userOnAppFavorited.onAppFavorited(innerIndex);
        }
    };

    /**
     *
     * @param gridResourceId
     * @param gridItemResourceId
     * @return
     */
    public static AppPageFragment newInstance(int position, int gridResourceId, int gridItemResourceId) {
        AppPageFragment appPage = new AppPageFragment();

        Bundle arguments = new Bundle();
        arguments.putInt("position", position);
        arguments.putInt("gridResourceId", gridResourceId);
        arguments.putInt("gridItemResourceId", gridItemResourceId);
        appPage.setArguments(arguments);

        return appPage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        appGrid = (AppPageDynamicGridView) inflater.inflate(gridResourceId, container, false);
        appGrid.setEditable(false);
        ((AppPageDynamicGridView) appGrid).setOnAppFavoritedListener(localOnAppFavorited);

        adapter = new AppDynamicGridAdapter(getActivity(), manager.getPage(position),
                gridItemResourceId);
        appGrid.setAdapter(adapter);

        return appGrid;
    }

    /**
     *
     * @param onAppFavorited
     */
    public void setAppFavoritedListener(OnAppFavoritedListener onAppFavorited) {
        this.userOnAppFavorited = onAppFavorited;

        if(appGrid != null)
            ((AppPageDynamicGridView) appGrid).setOnAppFavoritedListener(localOnAppFavorited);
    }
}
