package com.grioni.launcher.app;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Matias Grioni on 8/16/14.
 */
public class AppPageDynamicGridView extends DrawerPageDynamicGridView {

    private OnAppFavoritedListener onAppFavorited;
    private OnItemLongClickListener userItemLongClick = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(onAppFavorited != null) {
                onAppFavorited.onAppFavorited(position);
                return true;
            }

            return false;
        }
    };

    /**
     *
     * @param context
     */
    public AppPageDynamicGridView(Context context) {
        super(context);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public AppPageDynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AppPageDynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    @Override
    protected void init() {
        super.init();

        setEditable(false);
        setOnItemLongClickListener(userItemLongClick);
    }

    /**
     *
     * @param onAppFavorited
     */
    public void setOnAppFavoritedListener(OnAppFavoritedListener onAppFavorited) {
        this.onAppFavorited = onAppFavorited;
    }
}
