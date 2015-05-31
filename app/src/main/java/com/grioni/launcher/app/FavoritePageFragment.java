package com.grioni.launcher.app;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Matias Grioni on 7/22/14.
 */
public class FavoritePageFragment extends DrawerPageFragment {

    private DataSetObserver favoritesObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            adapter.setData(manager.getFavoritePage(position));
        }
    };

    private OnAppMovedListener onFavoriteMoved = new OnAppMovedListener() {
        @Override
        public void appMoved(int from, int to) {
            manager.updateFavorites(position, from, to);
        }
    };

    private OnFolderCreatedListener onFolderCreated = new OnFolderCreatedListener() {
        @Override
        public void onFolderAttempted(int rootPos, int intoPos) {
            manager.addFolder(rootPos, intoPos);
        }
    };

    private OnDeleteItemListener onDeleteItemListener = new OnDeleteItemListener() {
        @Override
        public void onItemDeleted(int position) {
            manager.deleteFavorite(position);
        }
    };

    /**
     *
     * @param gridResourceId
     * @param gridItemResourceId
     * @return
     */
    public static FavoritePageFragment newInstance(int position, int gridResourceId, int gridItemResourceId) {
        FavoritePageFragment appPage = new FavoritePageFragment();

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

        appGrid = (FavoritePageDynamicGridView) inflater.inflate(gridResourceId, container, false);
        appGrid.setOnAppMovedListener(onFavoriteMoved);
        ((FavoritePageDynamicGridView) appGrid).setOnFolderCreatedListener(onFolderCreated);
        appGrid.setOnDeleteItemListener(onDeleteItemListener);

        adapter = new FavoriteDynamicGridAdapter(getActivity(), manager.getFavoritePage(position), gridItemResourceId);
        appGrid.setAdapter(adapter);

        manager.registerObserver(favoritesObserver);

        return appGrid;
    }

    public void onDestroyView() {
        super.onDestroyView();
        manager.unregisterObserver(favoritesObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FavoritesWriteTask writeTask = new FavoritesWriteTask(getActivity());
        writeTask.execute(manager.getFavorites());
    }

    /**
     *
     * @param info
     */
    public void transferItem(AppTransferInfo info) {
        //((FavoritePageDynamicGridView) appGrid).transferItem(info);
    }
}
