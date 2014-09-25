package com.grioni.launcher.app;

import java.util.List;

/**
 * Created by Matias Grioni on 6/9/14.
 */
public interface PageListener {
    public void onPageChanged(int pageIndex, List<?> data);
    public void onPageCountChangedListener(int pageCount);

    public void onPageChangeRequest(Object item);
}
