package com.grioni.launcher.app;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Matias Grioni on 8/20/14.
 */
public class FavoriteDynamicGridAdapter extends DrawerDynamicGridAdapter<AppDrawerItem> {

    /**
     *
     * @param context
     * @param gridItemResourceId
     */
    public FavoriteDynamicGridAdapter(Context context, int gridItemResourceId) {
        super(context, gridItemResourceId);
    }

    /**
     * Creates a new adapter to use using parameters.
     *
     * @param context - Context to use for this adapter.
     * @param data - The data to be represented by this adapter.
     * @param gridItemResourceId - The xml resource to use to inflate individual items.
     */
    public FavoriteDynamicGridAdapter(Context context, List<AppDrawerItem> data, int gridItemResourceId) {
        super(context, data, gridItemResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View generated = super.getView(position, convertView, parent);

        return generated;
    }
}
