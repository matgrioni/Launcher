package com.grioni.launcher.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Matias Grioni on 8/25/14.
 */
public class DynamicListView extends ListView {

    private int downY;
    private int moveY;
    private int offsetY;

    private int mobileItemId;
    private int activePointerId;

    private Rect mobileItemCurrent;
    private Rect mobileItemOriginal;
    private Drawable mobileItemDrawable;

    private boolean editing;

    private List<Long> nonMobileIds;

    private OnItemLongClickListener userItemLongClick;
    private OnItemLongClickListener localItemLongClick = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            setEditing(true);

            view.setVisibility(View.INVISIBLE);
            mobileItemId = (int) getAdapter().getItemId(position);
            mobileItemDrawable = getHoverView(view);

            offsetY = 0;

            if(userItemLongClick != null)
                userItemLongClick.onItemLongClick(parent, view, position, id);


            return true;
        }
    };

    public DynamicListView(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public DynamicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        super.setOnItemLongClickListener(localItemLongClick);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getX();
                activePointerId = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if(mobileItemId == AbstractDynamicAdapter.INVALID_ID)
                    break;

                int pointerIndex = event.findPointerIndex(activePointerId);
                moveY = (int) event.getX(pointerIndex);

                int dy = moveY - downY;
                downY = moveY;

                mobileItemCurrent.offset(0, dy + offsetY);
                mobileItemDrawable.setBounds(mobileItemCurrent);

                invalidate();
                switchViews();

                return true;

            case MotionEvent.ACTION_UP:
                final View mobileView = getViewForId(mobileItemId);
                reset(mobileView);
        }

        return super.onTouchEvent(event);
    }

    /**
     *
     * @param v
     */
    private void reset(View v) {
        nonMobileIds.clear();
        editing = false;

        activePointerId = AbstractDynamicAdapter.INVALID_ID;
        mobileItemId = AbstractDynamicAdapter.INVALID_ID;
        mobileItemDrawable = null;

        if(v != null)
            v.setVisibility(View.VISIBLE);

        offsetY = 0;

        invalidate();
    }

    /**
     *
     */
    private void switchViews() {
        final View mobileView = getViewForId(mobileItemId);
        View targetView = null;
        int dy = moveY - downY;
        int vectorY = 0;

        for(int i = 0; i < nonMobileIds.size(); i++) {
            View currentView = getViewForId(nonMobileIds.get(i));

            if(mobileItemCurrent.centerY() < currentView.getTop()
                    || mobileItemCurrent.centerY() > currentView.getBottom()) {
                int itemDiffY = Math.abs(mobileItemCurrent.centerY() - DynamicGridUtils.getViewY(currentView));

                if(itemDiffY > vectorY) {
                    targetView = currentView;
                    vectorY = itemDiffY;
                }
            }
        }

        if(targetView != null) {
            final int original = getPositionForView(mobileView);
            final int target = getPositionForView(targetView);

            if(target == INVALID_POSITION) {
                updateNeighborIds(mobileItemId);
                return;
            }

            reorderItems(original, target);
            mobileView.setVisibility(View.VISIBLE);
            targetView.setVisibility(View.INVISIBLE);

            downY = moveY;
            offsetY += dy;

            updateNeighborIds(mobileItemId);

            // Animate the reordering of the switching of the views.
            final ViewTreeObserver observer = getViewTreeObserver();
            if(observer != null) {
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        //animateReorder(original, target);

                        return true;
                    }
                });
            }
        }
    }

    /**
     * Get the adapter of this DynamicGridView and reorder or switch the items.
     * @param original - The original position of the item to be moved.
     * @param target - The position the item in the location original should be moved to.
     */
    private void reorderItems(int original, int target) {
        ((AbstractDynamicAdapter) getAdapter()).reorderItems(original, target);
    }

    /**
     *
     * @param v
     * @return
     */
    private BitmapDrawable getHoverView(View v) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), getBitmapFromView(v));
        mobileItemOriginal = getRectFromView(v);
        mobileItemCurrent = new Rect(mobileItemOriginal);

        drawable.setBounds(mobileItemCurrent);

        return drawable;
    }

    /**
     * Creates a screenshot or image of the view passed in.
     * @param view - The view of which to create the BitmapDrawable for.
     * @return A BitmapDrawable of the view passed in.
     */
    protected Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     *
     * @param v
     * @return
     */
    private Rect getRectFromView(View v) {
        int width = v.getWidth();
        int height = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        return new Rect(left, top, left + width, top + height);
    }

    /**
     *
     * @param editing
     */
    protected void setEditing(boolean editing) {
        this.editing = editing;
    }

    private void updateNeighborIds(int baseId) {
       int basePosition = getPositionForId(baseId);
    }

    private int getPositionForId(int id) {
        View v = getViewForId(id);

        if(v == null)
            return -1;

        return getPositionForView(v);
    }

    private View getViewForId(long id) {
        int firstPosition = getFirstVisiblePosition();
        AbstractDynamicAdapter adapter = (AbstractDynamicAdapter) getAdapter();

        for(int i = firstPosition; i < getChildCount(); i++) {
            long itemId = adapter.getItemId(i);

            if(itemId == id)
                return getChildAt(i);
        }

        return null;
    }
}
