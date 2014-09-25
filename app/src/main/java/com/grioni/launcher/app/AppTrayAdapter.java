package com.grioni.launcher.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matias Grioni on 7/24/14.
 */
public class AppTrayAdapter extends BaseAdapter {
    private final long INVALID_ID = -1;
    private static final int APP_ITEMS_MAX = 5;

    private LayoutInflater inflater;
    private int itemResourceId;

    private List<AppModel> apps;
    private Map<AppModel, Integer> ids;

    /**
     *
     * @param context
     */
    public AppTrayAdapter(Context context, int itemResourceId) {
        this(context, null, itemResourceId);
    }

    /**
     *
     * @param context
     * @param apps
     * @param itemResourceId
     */
    public AppTrayAdapter(Context context, List<AppModel> apps, int itemResourceId) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemResourceId = itemResourceId;

        ids = new HashMap<AppModel, Integer>();

        if(apps != null)
            this.apps = new ArrayList<AppModel>(apps);
        else
            this.apps = new ArrayList<AppModel>();
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return apps.size();
    }

    /**
     *
     * @param position
     * @return
     */
    public AppModel getItem(int position) {
        return apps.get(position);
    }

    /**
     *
     * @param position
     * @param convertView
     * @param container
     * @return
     */
    public View getView(int position, View convertView, ViewGroup container) {
        View trayItem;
        AppModel cur = apps.get(position);

        if(convertView == null)
            trayItem = inflater.inflate(itemResourceId, container, false);
        else
            trayItem = convertView;

        ImageView icon = (ImageView) trayItem.findViewById(R.id.app_tray_icon);
        icon.setImageDrawable(cur.getIcon());

        return trayItem;
    }

    /**
     *
     * @return
     */
    public boolean hasStableIds() {
        return true;
    }

    /**
     *
     * @param data
     */
    public void setData(List<AppModel> data) {
        apps.clear();

        if(data != null) {
            if(data.size() > APP_ITEMS_MAX)
                apps = data.subList(0, APP_ITEMS_MAX);
            else
                apps = data;
            addAllIds(data);
        }

        notifyDataSetChanged();
    }

    /**
     *
     * @return
     */
    public List<AppModel> getData() {
        return apps;
    }

    public final long getItemId(int position) {
        if(position < 0 || position > ids.size() - 1)
            return INVALID_ID;

        AppModel app = getItem(position);
        return ids.get(app);
    }

    /**
     *
     * @param app
     */
    public void addId(AppModel app) {
        int newId = (int) getItemId(getCount() - 1) + 1;
        ids.put(app, newId);
    }

    /**
     *
     * @param apps
     */
    public void addAllIds(List<AppModel> apps) {
        int newId = (int) getItemId(getCount() - 1) + 1;

        for(int i = newId; i < apps.size(); i++) {
            ids.put(apps.get(i), i);
        }
    }

    /**
     *
     */
    protected void clearIds() {
        ids.clear();
    }

    /**
     *
     * @param app
     */
    protected void removeId(AppModel app) {
        ids.remove(app);
    }
}
