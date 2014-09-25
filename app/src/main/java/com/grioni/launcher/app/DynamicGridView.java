package com.grioni.launcher.app;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A GridView that can be reorganized by long clicking a view and dragging that view. The two items
 * will then be switched and on the up MotionEvent the editing is ended.
 *
 * Created by Matias Grioni on 5/15/14.
 */
public class DynamicGridView extends GridView {

    public static final int INVALID_ID = AbstractDynamicAdapter.INVALID_ID;

    private DynamicGridState gridState = DynamicGridState.STABLE;
    protected boolean editable = true;

    // The variables for the position of the down MotionEvent, move MotionEvent, the current offset
    // from the original location which is updated each time an item is switched, and the id of the
    // finger currently moving the item.
    private int downX, downY;
    private int moveX, moveY;
    private int offsetX, offsetY;
    private int activePointerId;

    private BitmapDrawable mobileItemDrawable;
    private int mobileItemId;

    private Rect mobileItemOriginal;
    private Rect mobileItemCurrent;

    protected List<Long> nonMobileIds = new ArrayList<Long>();

    private OnItemDroppedListener onItemDroppedListener;
    private OnAppMovedListener onAppMoved;

    private OnItemClickListener userItemClick;
    private OnItemClickListener localItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isEnabled() && userItemClick != null)
                userItemClick.onItemClick(parent, view, position, id);
        }
    };

    private OnItemLongClickListener userItemLongClick;
    private OnItemLongClickListener localItemLongClick = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(isEnabled()) {
                // Get the position on the GridView of the view that was long clicked by the location
                // of the down MotionEvent.
                int gridPosition = pointToPosition(downX, downY);

                mobileItemId = (int) getAdapter().getItemId(gridPosition);
                mobileItemDrawable = getHoverView(view);
                view.setVisibility(View.INVISIBLE);

                // Update all the possible ids with which the moving item can be switched with. The
                // updated list of view ids will then be checked for any possible switches.
                updateNeighborIds(mobileItemId);

                if(editable) {
                    gridState = DynamicGridState.EDITING;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    offsetX = 0;
                    offsetY = 0;
                }

                if(userItemLongClick != null)
                    userItemLongClick.onItemLongClick(parent, view, position, id);

                return true;
            }

            return false;
        }
    };

    /**
     * Creates a DynamicGridView using the context in which it is in.
     * @param context - The context in which the DynamicGridView is in.
     */
    public DynamicGridView(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a DynamicGridView using the context it is in, and any attributes for the view.
     * @param context - The context in which the DynamicGridView is in.
     * @param attrs - The attributes (xml) which define the appearance and behavior of the
     *              DynamicGridView.
     */
    public DynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates a DynamicGridView using the context it is in, attributes for the view from xml
     * resources, and a defined style resource for the view.
     * @param context - The context in which the DynamicGridView is in.
     * @param attrs - The attributes (xml) which define the appearance and behavior of the
     *              DynamicGridView.
     * @param defStyle - The defined style (xml resource) of the DynamicGridView.
     */
    public DynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    protected void init() {
        super.setOnItemLongClickListener(localItemLongClick);
    }

    /**
     * Sets the custom OnItemClickListener which is called by the local listener.
     * @param userItemClick - The user defined OnItemClickListener which is automatically called
     *                      by the local listener.
     */
    @Override
    public void setOnItemClickListener(OnItemClickListener userItemClick) {
        this.userItemClick = userItemClick;
        super.setOnItemClickListener(localItemClick);
    }

    /**
     * Sets the custom OnItemLongClickListener which is called by the local listener.
     * @param userItemLongClick - The user defined OnItemLongClickListener which is automatically
     *                          called by the local listener.
     */
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener userItemLongClick) {
        this.userItemLongClick = userItemLongClick;
        super.setOnItemLongClickListener(localItemLongClick);
    }

    /**
     *
     * @param onItemDroppedListener
     */
    public void setOnItemDroppedListener(OnItemDroppedListener onItemDroppedListener) {
        this.onItemDroppedListener = onItemDroppedListener;
    }

    /**
     *
     * @param onAppMoved
     */
    public void setOnAppMovedListener(OnAppMovedListener onAppMoved) {
        this.onAppMoved = onAppMoved;
    }

    /**
     * Called when there is a MotionEvent on this view.
     * @param event - The MotionEvent that occurred on the DynamicGridView.
     * @return - True if the event has been consumed and no more processing should happen, and false
     * if it has not been completely consumed and should still be processed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            // If there is a down MotionEvent then get the coordinates of the event and the id for
            // the pointer, or finger which is responsible for the event.
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                activePointerId = event.getPointerId(0);

                break;

            // If there is a move MotionEvent...
            case MotionEvent.ACTION_MOVE:
                if(mobileItemId == INVALID_ID)
                    break;

                if(gridState == DynamicGridState.STABLE) {
                    ((View) getParent()).onTouchEvent(event);
                    return false;
                }

                int pointerIndex = event.findPointerIndex(activePointerId);

                moveX = (int) event.getX(pointerIndex);
                moveY = (int) event.getY(pointerIndex);

                // Calculate the distance between the current pointer position and its position on
                // the down event.
                int dx = moveX - downX;
                int dy = moveY - downY;

                // If it is in edit mode, then move the current drawable of the moving item, by the
                // current change in position plus the total offset from moving around the positions.
                if(gridState == DynamicGridState.EDITING || gridState == DynamicGridState.FOLDER_CREATION) {
                    mobileItemCurrent.offsetTo(mobileItemOriginal.left + dx + offsetX,
                            mobileItemOriginal.top + dy + offsetY);
                    mobileItemDrawable.setBounds(mobileItemCurrent);

                    // Redraw the BitmapDrawable and then check if the position of the pointer is
                    // sufficient to switch views.
                    invalidate();

                    if (editable)
                        switchViews();

                    return false;
                }

                return false;

            // If there is an up MotionEvent then call reset for the DynamicGridView.
            case MotionEvent.ACTION_UP:
                final View mobileView = getViewForId(mobileItemId);
                reset(mobileView);

                if(onItemDroppedListener != null) {
                    int position = getPositionForId(mobileItemId);
                    onItemDroppedListener.onItemDropped(getAdapter().getItem(position), position);
                }

                break;

        }

        return super.onTouchEvent(event);
    }

    /**
     *
     */
    public int getMovingItemPosition() {
        return getPositionForId(mobileItemId);
    }

    /**
     * Switches around the views of the DynamicGridView according to their positions and the position
     * of the BitmapDrawable which is being moved by the user.
     */
    private void switchViews() {
        // Get the view that is currently being moved and its position in terms of the grid.
        final View mobileView = getViewForId(mobileItemId);
        Point mobileLoc = getColumnAndRowForView(mobileView);
        View targetView = null;

        // Find the movement of the view, or the user's finger from the last event including down or
        // touch.
        int dx = moveX - downX;
        int dy = moveY - downY;

        // Go through all the views other than the one being moved to check for movement. Movement
        // is checked by comparing the position of the moving item with the position of the current
        // view.
        for(int i = 0; i < nonMobileIds.size(); i++) {
            View currentView = getViewForId(nonMobileIds.get(i));
            Rect currentRect = getRectFromView(currentView);
            Point targetLoc = getColumnAndRowForView(currentView);

            int vectorX = 0;
            int vectorY = 0;

            // Check if the relative position of the view being checked against matches a position
            // of the BitmapDrawable to be switched. So that if the view is above and to the left,
            // then to be switched the BitmapDrawable has to be far enough up and to the left. If
            // more than 50% of the view is past the possible view to switch it with then switch it,
            // essentially.
            if(aboveLeft(targetLoc, mobileLoc)
                    && mobileItemCurrent.centerY() < currentRect.centerY()
                    && mobileItemCurrent.centerX() < currentRect.centerX()
                    || aboveRight(targetLoc, mobileLoc)
                    && mobileItemCurrent.centerY() < currentRect.centerY()
                    && mobileItemCurrent.centerX() > currentRect.centerX()
                    || belowLeft(targetLoc, mobileLoc)
                    && mobileItemCurrent.centerY() > currentRect.centerY()
                    && mobileItemCurrent.centerX() < currentRect.centerX()
                    || belowRight(targetLoc, mobileLoc)
                    && mobileItemCurrent.centerY() > currentRect.centerY()
                    && mobileItemCurrent.centerX() > currentRect.centerX()
                    || above(targetLoc, mobileLoc) && mobileItemCurrent.centerY() < currentRect.centerY()
                    || below(targetLoc, mobileLoc) && mobileItemCurrent.centerY() > currentRect.centerY()
                    || right(targetLoc, mobileLoc) && mobileItemCurrent.centerX() >currentRect.centerX()
                    || left(targetLoc, mobileLoc) && mobileItemCurrent.centerX() < currentRect.centerX()) {

                // If any of these possibilities is true, then get the difference between the centers
                // of the BitmapDrawable and the view being switched.
                int itemDiffX = Math.abs(mobileItemCurrent.centerX() - DynamicGridUtils.getViewX(currentView));
                int itemDiffY = Math.abs(mobileItemCurrent.centerY() - DynamicGridUtils.getViewY(currentView));

                // This is so that the targetView is assigned to the view which has the largest
                // absolute difference in position.
                if(itemDiffX > vectorX && itemDiffY > vectorY) {
                    vectorX = itemDiffX;
                    vectorY = itemDiffY;

                    targetView = currentView;
                }
            }
        }

        // If there was a targetView found which can be switched.
        if(targetView != null) {
            final int original = getPositionForView(mobileView);
            final int target = getPositionForView(targetView);

            // If the targetView is not a valid position then simply return.
            if(target == INVALID_POSITION) {
                updateNeighborIds(mobileItemId);
                return;
            }

            reorderItems(original, target);
            if(onAppMoved != null)
                onAppMoved.appMoved(original, target);

            mobileView.setVisibility(View.VISIBLE);
            targetView.setVisibility(View.INVISIBLE);

            downX = moveX;
            downY = moveY;

            offsetX += dx;
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
     * Resets the DynamicGridView so that all moving of views or editing is finished.
     * @param mobileView - The view that was being moved which should be set to visible.
     */
    private void reset(View mobileView) {
        nonMobileIds.clear();
        activePointerId = INVALID_ID;
        mobileItemId = INVALID_ID;
        mobileItemDrawable = null;

        gridState = DynamicGridState.STABLE;
        getParent().requestDisallowInterceptTouchEvent(false);

        offsetX = 0;
        offsetY = 0;

        if (mobileView != null)
            mobileView.setVisibility(View.VISIBLE);

        invalidate();
    }

    /**
     *
     * @param transferInfo
     */
    protected void startMovingData(AppTransferInfo transferInfo) {
        if(editable) {
            // AppTransferInfo.AppPosition location = transferInfo.appPosition;
            int position = transferInfo.position;

            offsetX = 0;
            offsetY = 0;

            mobileItemId = (int) getAdapter().getItemId(position);
            getMobileView().setVisibility(View.INVISIBLE);
            mobileItemDrawable = getHoverView(getMobileView());
            updateNeighborIds(mobileItemId);

            setGridState(DynamicGridState.EDITING);
        }
    }

    /**
     *
     * @param view - The view to find the position for.
     * @return - A Point that represents the location the parameter, view. The x coordinate represents
     * the column of the view and the y coordinate represents the row of the view.
     */
    private Point getColumnAndRowForView(View view) {
        int pos = getPositionForView(view);
        int column = pos % getNumColumns();
        int row = pos / getNumColumns();

        return new Point(column, row);
    }

    /**
     * Get a view that is represented by a point, where x is the column, and y the row.
     * @param point - The point or position of the view to get.
     * @return - The view that is represented by the point passed in.
     */
    private View getViewForPoint(Point point) {
        int position = point.y * getNumColumns() + point.x;
        int visPosition = position - getFirstVisiblePosition();

        return getChildAt(visPosition);

    }

    /**
     * Gets a Bitmap of the View and creates a BitmapDrawable and assigns the Rect's of the mobile
     * item properly.
     * @param view - The View to get the BitmapDrawable of.
     * @return - The BitmapDrawable of the View.
     */
    private BitmapDrawable getHoverView(View view) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), getBitmapFromView(view));
        mobileItemOriginal = getRectFromView(view);
        mobileItemCurrent = new Rect(mobileItemOriginal);

        drawable.setBounds(mobileItemCurrent);

        return drawable;
    }

    /**
     *
     * @param view
     * @return
     */
    private Rect getRectFromView(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        int top = view.getTop();
        int left = view.getLeft();

        return new Rect(left, top, left + width, top + height);
    }

    /**
     * Draws the DynamicGridView and also the floating and moving BitmapDrawable if there is one.
     * @param canvas - The canvas to draw into.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mobileItemDrawable != null)
            mobileItemDrawable.draw(canvas);
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
     * Update the list of the non mobile ids so that it includes all the Views except the one being
     * moved.
     * @param itemId - The id of the moving item.
     */
    private void updateNeighborIds(long itemId) {
        int movingPosition = getPositionForId(itemId);

        for(int i = getFirstVisiblePosition(); i < getLastVisiblePosition() + 1; i++) {
            if(movingPosition != i)
                nonMobileIds.add(getId(i));
        }
    }

    /**
     *
     * @param id
     * @return
     */
    private int getPositionForId(long id) {
        View v = getViewForId(id);

        if(v == null)
            return -1;

        return getPositionForView(v);
    }

    /**
     *
     * @return
     */
    public View getMobileView() {
        return getViewForId(mobileItemId);
    }

    /**
     *
     * @param position
     * @return
     */
    private long getId(int position) {
        return getAdapter().getItemId(position);
    }

    /**
     *
     * @param id
     * @return
     */
    protected View getViewForId(long id) {
        int firstVisiblePosition = getFirstVisiblePosition();
        AbstractDynamicAdapter adapter = (AbstractDynamicAdapter) getAdapter();

        for(int i = 0; i < getChildCount(); i++) {
            int position = firstVisiblePosition + i;
            long itemId = adapter.getItemId(position);

            if(itemId == id)
                return getChildAt(i);
        }

        return null;
    }

    private boolean belowLeft(Point first, Point second) {
        return (first.y > second.y && first.x < second.x);
    }

    private boolean belowRight(Point first, Point second) {
        return (first.y > second.y && first.x > second.x);
    }

    private boolean aboveLeft(Point first, Point second) {
        return (first.y < second.y && first.x < second.x);
    }

    private boolean aboveRight(Point first, Point second) {
        return (first.y < second.y && first.x > second.x);
    }

    private boolean above(Point first, Point second) {
        return (first.y < second.y && first.x == second.x);
    }

    private boolean below(Point first, Point second) {
        return (first.y > second.y && first.x == second.x);
    }

    private boolean right(Point first, Point second) {
        return (first.y == second.y && first.x > second.x);
    }

    private boolean left(Point first, Point second) {
        return (first.y == second.y && first.x < second.x);
    }

    /**
     *
     * @return
     */
    protected Rect getMobileItemCurrent() {
        return mobileItemCurrent;
    }

    protected int getDownX() {
        return downX;
    }

    protected int getDownY() {
        return downY;
    }

    /**
     *
     * @return
     */
    protected int getOffsetX() {
        return offsetX;
    }

    protected int getOffsetY() {
        return offsetY;
    }

    protected BitmapDrawable getMobileItemDrawable() {
        return mobileItemDrawable;
    }

    /**
     *
     * @return
     */
    public DynamicGridState getGridState() {
        return gridState;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     *
     * @param gridState
     */
    public void setGridState(DynamicGridState gridState) {
        this.gridState = gridState;
    }
}
