package com.grioni.launcher.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends AbstractDynamicGridAdapter and is intended to define the behavior for handling
 * the data that is being adapted. For example, adding or removing an item also involves changing
 * the id map of AbstractDynamicGridAdapter to represent these modifications, and also the adapter
 * must be notified that the data set has changed.
 *
 * Created by Matias Grioni on 5/12/14.
 */
public abstract class DynamicGridAdapter<T> extends AbstractDynamicAdapter<T> {

    private List<T> data;

    /**
     *
     * @param context
     * @param gridItemResourceId
     */
    public DynamicGridAdapter(Context context, int gridItemResourceId) {
        this(context, null, gridItemResourceId);
    }

    /**
     *
     * @param context
     * @param data
     * @param itemResourceId
     */
    public DynamicGridAdapter(Context context, List<T> data, int itemResourceId) {
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
        if(newPosition < getCount()) {
            DynamicGridUtils.reorder(data, oldPosition, newPosition);
            notifyDataSetChanged();
        }
    }

    /**
     *
     * @param item
     */
    public void add(T item) {
        data.add(item);
        addId(item);

        notifyDataSetChanged();
    }

    /**
     *
     * @param item
     */
    public void remove(T item) {
        data.remove(item);
        removeId(item);

        notifyDataSetChanged();
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
     * @return
     */
    public int getCount() {
        return data.size();
    }

    public List<T> getItems() {
        return data;
    }

    /**
     *
     * @param data - The data to be represented by the adapter.
     */
    public void setData(List<T> data) {
        this.data.clear();
        clearIds();

        if(data != null) {
            this.data.addAll(data);
            addAllIds(data);
        }

        notifyDataSetChanged();
    }
}
