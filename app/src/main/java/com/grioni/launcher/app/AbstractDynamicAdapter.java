package com.grioni.launcher.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class to represent an adapter for a DynamicGridView. The GridView is composed of items,
 * which each have an id and can be modified, such as reordered dynamically, depending on user
 * interaction.
 *
 * Created by Matias Grioni on 5/15/14.
 */
public abstract class AbstractDynamicAdapter<T> extends BaseAdapter {

    public static final int INVALID_ID = -1;

    /**
     * The Map of items as the key and the id of that item as the value. A map is used rather than a
     * list and using their index as an id so that the items are more flexible in their arrangement.
     */
    private Map<T, Integer> ids = new HashMap<T, Integer>();

    protected LayoutInflater inflater;
    protected int itemResourceId;
    protected Context context;

    /**
     * Reorders the items in the adapter from oldPosition to newPosition.
     * @param oldPosition - The position in the adapter that represents the item to be moved.
     * @param newPosition - The position to which this item should be moved.
     */
    public abstract void reorderItems(int oldPosition, int newPosition);

    /**
     *
     * @return The adapter has stable ids for each item which are unique for each data member.
     */
    @Override
    public final boolean hasStableIds() {
        return true;
    }

    /**
     * Adds an id to the list of ids in the adapter. The id of the new item is the id of the last
     * item in the list of data, plus one.
     *
     * @param item - The item to be added to the adapter.
     */
    protected void addId(T item) {
        int newId = (int) getItemId(getCount() - 1) + 1;
        ids.put(item, newId);
    }

    /**
     * Called when items has been added to the data of the adapter and the data needs
     * to be given ids.
     *
     * @param items - The items that have been added to the adapter and need ids.
     */
    protected void addAllIds(List<T> items) {
        int newId = (int) getItemId(getCount() - 1) + 1;

        for(int i = newId; i < items.size(); i++) {
            T item = items.get(i);
            ids.put(item, i);
        }
    }

    /**
     *
     * @param position - The position of the item of which to get the id.
     * @return The id of the item at the position, position, in the adapter list of data.
     */
    public final long getItemId(int position) {
        if(position < 0 || position > ids.size() - 1)
            return INVALID_ID;

        T item = (T) getItem(position);
        return ids.get(item);
    }

    /**
     * Called when the adapter data is cleared to clear the ids of the items in the adapter.
     */
    protected void clearIds() {
        ids.clear();
    }

    /**
     * Removes the id of an item from the Map of ids for the adapter.
     *
     * @param item - The item in the adapter data of which to remove the id of from the Map
     *             of ids.
     */
    protected void removeId(T item) {
        ids.remove(item);
    }

    /**
     *
     * @return
     */
    public int getItemResourceId() {
        return itemResourceId;
    }
}
