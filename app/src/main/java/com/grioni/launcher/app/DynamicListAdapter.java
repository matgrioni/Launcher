package com.grioni.launcher.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matias Grioni on 8/25/14.
 */
public class DynamicListAdapter<T> extends AbstractDynamicAdapter {

    private Map<T, Integer> ids = new HashMap<T, Integer>();
    private List<T> data;

    /**
     *
     * @param context
     * @param itemResourceId
     */
    public DynamicListAdapter(Context context, int itemResourceId) {
        this(context, null, itemResourceId);
    }

    /**
     *
     * @param context
     * @param data
     * @param itemResourceId
     */
    public DynamicListAdapter(Context context, List<T> data, int itemResourceId) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(data != null)
            this.data = data;
        else
            this.data = new ArrayList<T>();
        addAllIds(this.data);


        this.itemResourceId = itemResourceId;
    }

    /**
     *
     * @param oldPosition - The position in the adapter that represents the item to be moved.
     * @param newPosition - The position to which this item should be moved.
     */
    public void reorderItems(int oldPosition, int newPosition) {
        DynamicGridUtils.reorder(data, oldPosition, newPosition);
    }

    /**
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position
     * @param convertView
     * @param container
     * @return
     */
    public View getView(int position, View convertView, ViewGroup container) {
        return null;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return data.size();
    }
}
