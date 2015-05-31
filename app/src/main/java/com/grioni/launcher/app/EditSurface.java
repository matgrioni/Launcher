package com.grioni.launcher.app;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 *
 *
 * Created by Matias Grioni on 11/14/14.
 */
public interface EditSurface {
    /**
     *
     * @return
     */
    public BitmapDrawable getMobileDrawable();

    /**
     *
     * @return
     */
    public Rect getMobileItemPosition();

}
