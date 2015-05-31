package com.grioni.launcher.app;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Matias Grioni on 7/19/14.
 */
public class AppTrayFragment extends Fragment {

    private AppTrayContainer appTray;
    private AppTrayAdapter adapter;
    private AppManager manager;

    /**
     *
     * @return
     */
    public static AppTrayFragment newInstance() {
        return new AppTrayFragment();
    }

    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            update();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        manager = AppManager.getInstance();
        manager.registerObserver(observer);

        appTray = (AppTrayContainer) inflater.inflate(R.layout.fragment_app_tray, container, false);
        adapter = new AppTrayAdapter(getActivity(), manager.getAppTray(), R.layout.app_tray_item);
        appTray.setAdapter(adapter);

        return appTray;
    }

    /**
     *
     */
    public void update() {
        adapter.setData(manager.getAppTray());
    }

}
