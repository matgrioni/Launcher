package com.grioni.launcher.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Matias Grioni on 9/5/14.
 */
public class WidgetCardFragment extends CardFragment {

    private String packageName;

    /**
     *
     * @param layoutId
     * @param packageName
     * @return
     */
    public WidgetCardFragment newInstance(int layoutId, String packageName) {
        WidgetCardFragment widgetCard = new WidgetCardFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(LAYOUT_KEY, layoutId);
        arguments.putString("packageName", packageName);
        widgetCard.setArguments(arguments);

        return widgetCard;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        packageName = arguments.getString("packageName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return null;
    }
}
