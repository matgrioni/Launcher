package com.grioni.launcher.app;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Matias Grioni on 5/16/14.
 */
public class DynamicGridUtils {

    private static int height;
    private boolean instantiated = false;

    public static void instantiate(Context context) {
        height = context.getResources().getDisplayMetrics().heightPixels;

        if(context instanceof ActionBarActivity)
            height -= 2 * ((ActionBarActivity) context).getActionBar().getHeight();
    }

    /**
     *
     * @param data
     * @param oldPosition
     * @param newPosition
     */
    public static void reorder(List data, int oldPosition, int newPosition) {
        Collections.swap(data, oldPosition, newPosition);
    }

    /**
     *
     * @param view
     * @return
     */
    public static int getViewX(View view) {
        return (view.getLeft() + view.getRight()) / 2;
    }

    /**
     *
     * @param view
     * @return
     */
    public static int getViewY(View view) {
        return (view.getTop() + view.getBottom()) / 2;
    }

    /**
     *
     * @param iconHeight
     * @param padding
     * @return
     */
    public static int getRows(int iconHeight, int padding) {
        int usableSpace = height - padding;
        float exactRows = (float) usableSpace / (iconHeight + padding);

        // This figure is the amount of rows that will be used in this display, being the screen.
        return (int) (exactRows + 0.5f);
    }
}
