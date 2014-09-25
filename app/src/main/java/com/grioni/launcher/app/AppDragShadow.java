package com.grioni.launcher.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Matias Grioni on 8/20/14.
 */
public class AppDragShadow extends View.DragShadowBuilder {
    private static Drawable shadow;

    /**
     *
     * @param v
     */
    public AppDragShadow(View v) {
        super(v);
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        int width = getView().getWidth() / 2;
        int height = getView().getHeight() / 2;

        shadow.setBounds(0, 0, width, height);

        size.set(width, height);
        touch.set(width, height);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
