package com.grioni.launcher.app;

import android.content.Context;
import android.os.Parcel;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A DynamicGridAdapter specifically for AppModel objects.
 *
 * Created by Matias Grioni on 5/16/14.
 */
public class DrawerDynamicGridAdapter<T extends AppDrawerItem> extends DynamicGridAdapter<T> {

    /**
     *
     * @param context
     * @param gridItemResourceId
     */
    public DrawerDynamicGridAdapter(Context context, int gridItemResourceId) {
        super(context, gridItemResourceId);
    }

    /**
     * Creates a new adapter to use using parameters.
     *
     * @param context - Context to use for this adapter.
     * @param data - The data to be represented by this adapter.
     * @param gridItemResourceId - The xml resource to use to inflate individual items.
     */
    public DrawerDynamicGridAdapter(Context context, List<T> data, int gridItemResourceId) {
        super(context, data, gridItemResourceId);
    }

    /**
     * Create a view for a certain item represented by the adapter.
     *
     * @param position - The position of the item to create the view for.
     * @param convertView - Recyclable view.
     * @param parent - The parent of the view to be created.
     * @return - The created view for the item.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View appView;
        if(convertView == null)
            appView = inflater.inflate(itemResourceId, parent, false);
        else
            appView = convertView;

        ImageView icon = (ImageView) appView.findViewById(R.id.app_icon);
        TextView label = (TextView) appView.findViewById(R.id.app_label);

        AppDrawerItem app = getItem(position);
        icon.setImageDrawable(app.getIcon());
        label.setText(app.getLabel());

        return appView;
    }
}
