package com.grioni.launcher.app;

import android.graphics.drawable.Drawable;

/**
 * Created by Matias Grioni on 8/7/14.
 */
public abstract class AppDrawerItem {
    protected String label;
    protected Drawable icon;

    /**
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @return
     */
    public Drawable getIcon() {
        return icon;
    }

}
