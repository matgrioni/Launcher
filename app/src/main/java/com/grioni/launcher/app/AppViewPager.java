package com.grioni.launcher.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Matias Grioni on 7/31/14.
 */
public class AppViewPager extends ViewPager {

    // The variables for the position of the down MotionEvent, move MotionEvent, the current offset
    // from the original location which is updated each time an item is switched, and the id of the
    // finger currently moving the item.
    private int downX, downY;
    private int moveX, moveY;
    private int offsetX, offsetY;
    private int activePointerId;

    private BitmapDrawable mobileItemDrawable;
    private int mobileItemId;

    /**
     *
     * @param context
     */
    public AppViewPager(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public AppViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;


        // Allow the children of the view pager to still get the MotionEvent.
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


    }
}
