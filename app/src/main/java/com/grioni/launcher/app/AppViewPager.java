package com.grioni.launcher.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Matias Grioni on 7/31/14.
 */
public class AppViewPager extends ViewPager {

    private BitmapDrawable mobileItemDrawable;
    private int mobileX;
    private int mobileY;

    /**
     *
     * @param context
     */
    AppViewPager(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    AppViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
