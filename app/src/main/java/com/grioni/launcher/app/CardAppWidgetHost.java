package com.grioni.launcher.app;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

/**
 * Created by Matias Grioni on 9/5/14.
 */
public class CardAppWidgetHost extends AppWidgetHost {

    /**
     *
     * @param activity
     * @param hostId
     */
    public CardAppWidgetHost(Activity activity, int hostId) {
        super(activity, hostId);
    }

    @Override
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
                                             AppWidgetProviderInfo info) {
        return new CardAppWidgetHostView(context);
    }

    @Override
    public void stopListening() {
        super.stopListening();
        clearViews();
    }

    /**
     *
     */
    protected void onProvidersChanged() {
        // TODO: Do what is in the android launcher source.
    }
}
