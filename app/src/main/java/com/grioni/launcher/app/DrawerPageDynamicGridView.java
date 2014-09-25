package com.grioni.launcher.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Matias Grioni on 8/16/14.
 */
public class DrawerPageDynamicGridView extends PageDynamicGridView {

    private OnDeleteItemListener onDeleteItem;
    private int deleteHeight;

    private OnItemClickListener userItemClick;
    private OnItemClickListener localItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AppDrawerItem item = (AppDrawerItem) getAdapter().getItem(position);

            if(userItemClick != null)
                userItemClick.onItemClick(parent, view, position, id);

            if(item instanceof AppModel) {
                Intent intent = getContext().getPackageManager()
                        .getLaunchIntentForPackage(((AppModel) item).getPackageName());

                if (intent != null)
                    getContext().startActivity(intent);
            }
        }
    };

    /**
     *
     * @param context
     */
    public DrawerPageDynamicGridView(Context context) {
        super(context);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public DrawerPageDynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public DrawerPageDynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initAttrs(attrs);
    }

    @Override
    protected void init() {
        super.init();
        super.setOnItemClickListener(localItemClick);
    }


    /**
     *
     * @param attrs
     */
    public void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DrawerPageDynamicGridView);
        deleteHeight = (int) typedArray.getDimension(R.styleable.DrawerPageDynamicGridView_deleteHeight, 0);

        super.setOnItemClickListener(localItemClick);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch(action) {
            case MotionEvent.ACTION_UP:
                if (getGridState() == DynamicGridState.FOLDER_CREATION || getGridState() == DynamicGridState.EDITING) {
                    if (getMobileItemCurrent().centerY() > getBottom() - deleteHeight) {
                        if (onDeleteItem != null) {
                            // TODO: This is not the best way to solve the problem of visibility for
                            // a view that is just added after deletion but it works.
                            getMobileView().setVisibility(View.VISIBLE);
                            onDeleteItem.onItemDeleted(getMovingItemPosition());
                        }
                    }
                }

                setGridState(DynamicGridState.STABLE);

                break;
        }

        return super.onTouchEvent(event);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(getGridState() == DynamicGridState.EDITING
                || getGridState() == DynamicGridState.FOLDER_CREATION) {
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.delete_zone_background));
            canvas.drawRect(0, getBottom() - deleteHeight,
                    getRight(), getBottom(), paint);
        }
    }

    /**
     *
     * @param onDeleteItem
     */
    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItem) {
        this.onDeleteItem = onDeleteItem;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener userItemClick) {
        this.userItemClick = userItemClick;
        super.setOnItemClickListener(localItemClick);
    }

    /**
     *
     * @return
     */
    protected OnDeleteItemListener getOnDeleteItemListener() {
        return onDeleteItem;
    }

    /**
     *
     * @param deleteHeight
     */
    public void setDeleteHeight(int deleteHeight) {
        this.deleteHeight = deleteHeight;
    }

    /**
     *
     * @return
     */
    public int getDeleteHeight() {
        return deleteHeight;
    }
}
