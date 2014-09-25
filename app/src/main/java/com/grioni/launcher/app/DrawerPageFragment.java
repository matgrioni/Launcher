package com.grioni.launcher.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

/**
 * Created by Matias Grioni on 7/24/14.
 */
public abstract class DrawerPageFragment extends Fragment {

    protected DrawerPageDynamicGridView appGrid;
    protected DrawerDynamicGridAdapter adapter;
    protected AppManager manager;

    protected int position;
    protected int pageLimit;
    protected int gridResourceId;
    protected int gridItemResourceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        position = arguments.getInt("position");
        gridResourceId = arguments.getInt("gridResourceId");
        gridItemResourceId = arguments.getInt("gridItemResourceId");

        calcPageLimit();
        manager = AppManager.getInstance(pageLimit);
    }

    private void calcPageLimit() {
        int iconSize = (int) getResources().getDimension(R.dimen.app_icon_size);
        int padding = (int) getResources().getDimension(R.dimen.app_vertical_margin);

        pageLimit = DynamicGridUtils.getRows(iconSize, padding) * 4;
    }

    /**
     *
     * @param data
     */
    public void setData(List<AppDrawerItem> data) {
        adapter.setData(data);
    }

    /**
     *
     * @param adapter
     */
    public void setAdapter(AppDynamicGridAdapter adapter) {
        this.adapter = adapter;
        if(appGrid != null)
            appGrid.setAdapter(adapter);
    }
}
