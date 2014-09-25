package com.grioni.launcher.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Matias Grioni on 8/26/14.
 */

public class CardFragment extends Fragment {
    protected static final String LAYOUT_KEY = "layoutResourceId";

    protected View cardView;
    private int layoutResourceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        layoutResourceId =  arguments.getInt(LAYOUT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        cardView = inflater.inflate(layoutResourceId, container, false);

        return cardView;
    }
}