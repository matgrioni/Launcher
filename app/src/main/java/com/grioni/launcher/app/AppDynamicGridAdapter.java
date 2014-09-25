package com.grioni.launcher.app;

import android.content.ClipDescription;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Matias Grioni on 8/20/14.
 */
public class AppDynamicGridAdapter extends DrawerDynamicGridAdapter<AppModel> {

    /**
     *
     * @param context
     * @param gridItemResourceId
     */
    public AppDynamicGridAdapter(Context context, int gridItemResourceId) {
        super(context, gridItemResourceId);
    }

    /**
     *
     * @param context
     * @param data
     * @param gridItemResourceId
     */
    public AppDynamicGridAdapter(Context context, List<AppModel> data, int gridItemResourceId) {
        super(context, data, gridItemResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        View generated = super.getView(position, convertView, container);

        return generated;
    }
}
