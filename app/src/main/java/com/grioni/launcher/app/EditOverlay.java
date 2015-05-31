package com.grioni.launcher.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * A class that handles the editing of a child view. A child view will receive
 * an edit command. This will send a callback and drawable object and position
 * info to the class to draw this object. This way the state of the underlying
 * children can be changed while not losing the MotionEvent.
 *
 * Class extends RelativeLayout rather than ViewGroup because overriding
 * onLayout is unnecessary for what is needed from the class. This should simply
 * act as a layer over any child views.
 *
 * Created by Matias Grioni on 11/14/14.
 */
public class EditOverlay extends RelativeLayout {

    /**
     *
     * @param context
     */
    public EditOverlay(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public EditOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public EditOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
