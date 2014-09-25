package com.grioni.launcher.app;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Matias Grioni on 8/5/14.
 */
public class AppTransferInfo {
    public int position;

    public AppPosition appPosition;

    /**
     *
     */
    public class AppPosition {
        public int x;
        public int y;
        public int offsetX;
        public int offsetY;

        /**
         *
         * @param x
         * @param y
         * @param offsetX
         * @param offsetY
         */
        public AppPosition(int x, int y, int offsetX, int offsetY) {
            this.x = x;
            this.y = y;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    /**
     *
     * @param position
     * @param x
     * @param y
     */
    public AppTransferInfo(int position, int x, int y, int offsetX, int offsetY) {
        this.position = position;
        appPosition = new AppPosition(x, y, offsetX, offsetY);
    }
}
