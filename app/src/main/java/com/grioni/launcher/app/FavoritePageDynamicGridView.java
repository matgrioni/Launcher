package com.grioni.launcher.app;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Matias Grioni on 8/16/14.
 */
public class FavoritePageDynamicGridView extends DrawerPageDynamicGridView {

    private OnFolderCreatedListener onFolderCreated;

    private Drawable priorIcon;
    private Drawable folderIcon;
    private int rootId;

    private OnItemClickListener userItemClick;
    private OnItemClickListener localItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isEnabled() && userItemClick != null)
                userItemClick.onItemClick(parent, view, position, id);

            AppDrawerItem item = (AppDrawerItem) getAdapter().getItem(position);

            if(item instanceof AppDrawerFolder) {
                PopupWindow popup = createFolderPopup((AppDrawerFolder) item, R.layout.folder_grid);

                popup.showAsDropDown(view);
            }
        }
    };

    /**
     *
     * @param context
     */
    public FavoritePageDynamicGridView(Context context) {
        super(context);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public FavoritePageDynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FavoritePageDynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    @Override
    protected void init() {
        super.init();
        super.setOnItemClickListener(localItemClick);

        folderIcon = getResources().getDrawable(R.drawable.folder);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int possibleRootId = INVALID_ID;

                for(int i = 0; i < nonMobileIds.size(); i++) {
                    View neighborView = getViewForId(nonMobileIds.get(i));

                    Rect mobileItemCurrent = getMobileItemCurrent();
                    int midXRight = neighborView.getRight();
                    int midXLeft = neighborView.getLeft();
                    int midYTop = neighborView.getTop();
                    int midYBottom = neighborView.getBottom();

                    if (mobileItemCurrent.centerX() < midXRight && mobileItemCurrent.centerX() > midXLeft
                            && mobileItemCurrent.centerY() < midYBottom && mobileItemCurrent.centerY() > midYTop) {
                        possibleRootId = nonMobileIds.get(i).intValue();
                        rootId = nonMobileIds.get(i).intValue();
                        break;
                    }
                }

                if(possibleRootId != INVALID_ID && getGridState() == DynamicGridState.EDITING) {
                    setGridState(DynamicGridState.FOLDER_CREATION);

                    ImageView icon = (ImageView) getViewForId(possibleRootId).findViewById(R.id.app_icon);
                    priorIcon = icon.getDrawable();
                    icon.setImageDrawable(folderIcon);
                } else if(possibleRootId == INVALID_ID && getGridState() == DynamicGridState.FOLDER_CREATION) {
                    setGridState(DynamicGridState.EDITING);

                    ImageView icon = (ImageView) getViewForId(rootId).findViewById(R.id.app_icon);
                    icon.setImageDrawable(priorIcon);
                }

                break;

            case MotionEvent.ACTION_UP:
                if(getGridState() == DynamicGridState.FOLDER_CREATION) {
                    View folderRoot = getViewForId(rootId);

                    // TODO: Same thing as above where this is not possibly the best way to do this.
                    getMobileView().setVisibility(View.VISIBLE);
                    onFolderCreated.onFolderAttempted(getPositionForView(folderRoot),
                            getMovingItemPosition());
                }

                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     *
     * @param folder
     * @return
     */
    private PopupWindow createFolderPopup(AppDrawerFolder folder, int resourceId) {
        View contentView = inflate(getContext(), resourceId, null);

        TextView label = (TextView) contentView.findViewById(R.id.folder_label);
        DynamicGridView grid = (DynamicGridView) contentView.findViewById(R.id.folder_grid);
        grid.setOnItemClickListener(userItemClick);

        int gridItemResourceId = ((AbstractDynamicAdapter) getAdapter()).getItemResourceId();

        label.setText(folder.getLabel());
        grid.setAdapter(new AppDynamicGridAdapter(getContext(), folder.getMemberApps(), gridItemResourceId));

        PopupWindow folderPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        folderPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
        folderPopup.setOutsideTouchable(true);
        folderPopup.setFocusable(true);

        return folderPopup;
    }


    /**
     *
     * @param onFolderCreated
     */
    public void setOnFolderCreatedListener(OnFolderCreatedListener onFolderCreated) {
        this.onFolderCreated = onFolderCreated;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener userItemClick) {
        this.userItemClick = userItemClick;
        super.setOnItemClickListener(userItemClick);
    }
}
