package com.grioni.launcher.app;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

/**
 * Created by Matias Grioni on 7/27/14.
 */
public class AppTrayContainer extends LinearLayout {

    private AppTrayAdapter adapter;
    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            refreshAppViews();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
        }
    };

    private OnClickListener userClickListener;
    private OnClickListener localClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = indexOfChild(v);
            AppModel app = adapter.getItem(position);

            if (userClickListener != null)
                userClickListener.onClick(v);

            if(app != null) {
                Intent intent = getContext().getPackageManager()
                        .getLaunchIntentForPackage(app.getPackageName());

                if(intent != null)
                    getContext().startActivity(intent);
            }
        }
    };


    /**
     *
     * @param context
     */
    public AppTrayContainer(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public AppTrayContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AppTrayContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @return
     */
    public AppTrayAdapter getAdapter() {
        return adapter;
    }

    /**
     *
     * @param adapter
     */
    public void setAdapter(AppTrayAdapter adapter) {
        if(this.adapter != null)
            this.adapter.unregisterDataSetObserver(observer);
        this.adapter = adapter;

        if(adapter != null)
            this.adapter.registerDataSetObserver(observer);

        initAppViews();
    }

    /**
     *
     * @param itemClicked
     */
    public void setItemClickListener(OnClickListener itemClicked) {
        userClickListener = itemClicked;

        for(int i = 0; i < getChildCount(); i++)
            getChildAt(i).setOnClickListener(userClickListener);
    }

    /**
     *
     */
    protected void refreshAppViews() {
        int childCount = getChildCount();
        int reuseCount = Math.min(childCount, adapter.getCount());

        for(int i = 0; i < reuseCount; i++)
            adapter.getView(i, getChildAt(i), this);

        if(childCount < adapter.getCount()) {
            for(int i = childCount; i < adapter.getCount(); i++) {
                View newAppView = adapter.getView(i, null, this);
                newAppView.setOnClickListener(localClickListener);

                addView(newAppView, i);
            }
        } else if(childCount > adapter.getCount())
            removeViews(adapter.getCount(), childCount);
    }

    /**
     *
     */
    protected  void initAppViews() {
        removeAllViews();

        if(adapter.getData().size() != 0) {
            for(int i = 0; i < adapter.getCount(); i++) {
                View newAppView = adapter.getView(i, null, this);
                newAppView.setOnClickListener(localClickListener);

                addView(newAppView, i);
            }
        }
    }
}
