package com.grioni.launcher.app;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matias Grioni on 8/29/14.
 */
public class CardNotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification statusBar) {
        Notification notification = statusBar.getNotification();

        if(notification != null) {
            Intent intent = new Intent(MainActivity.INTENT_ACTION_NOTIFICATION_POSTED);
            intent.putExtra("notification", notification);

            sendBroadcast(intent);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification statusBar) {
        Notification notification = statusBar.getNotification();

        if(notification!= null) {
            Intent intent = new Intent(MainActivity.INTENT_ACTION_NOTIFICATION_REMOVED);
            intent.putExtra("notification", notification);

            sendBroadcast(intent);
        }
    }
}
