package com.grioni.launcher.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;

/**
 * Created by Matias Grioni on 7/8/14.
 */
public class PageDynamicGridView extends DynamicGridView {
    private int rows;

    /**
     *
     * @param context
     */
    public PageDynamicGridView(Context context) {
        super(context);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public PageDynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PageDynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    /**
     *
     */
    @Override
    protected void init() {
        super.init();

        int vPadding = (int) getContext().getResources().getDimension(R.dimen.app_vertical_margin);
        int iconSize = (int) getContext().getResources().getDimension(R.dimen.app_icon_size);
        rows = DynamicGridUtils.getRows(iconSize, vPadding);
    }

    /**
     *
     * @return
     */
    public int getRows() {
        return rows;
    }

    /**
     *
     * @param rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

}
