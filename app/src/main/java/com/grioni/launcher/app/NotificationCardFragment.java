package com.grioni.launcher.app;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Matias Grioni on 8/27/14.
 */
public class NotificationCardFragment extends CardFragment {

    private ImageView icon;
    private TextView title;
    private TextView content;
    private TextView contentInfo;
    private TextView timestamp;

    private Bundle notificationExtras;
    private Notification notification;

    /**
     *
     * @return
     */
    public static NotificationCardFragment newInstance(int layoutResourceId,
                                                       Notification notification) {
        NotificationCardFragment cardFragment = new NotificationCardFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(LAYOUT_KEY, layoutResourceId);
        arguments.putParcelable("notification", notification);
        cardFragment.setArguments(arguments);

        return cardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        notification = arguments.getParcelable("notification");
        notificationExtras = notification.extras;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        title = (TextView) cardView.findViewById(R.id.fragment_card_notif_title);
        content = (TextView) cardView.findViewById(R.id.fragment_card_notif_content);
        contentInfo = (TextView) cardView.findViewById(R.id.fragment_card_notif_content_info);
        timestamp = (TextView) cardView.findViewById(R.id.fragment_card_notif_timestamp);

        icon = (ImageView) cardView.findViewById(R.id.fragment_card_notif_icon);

        loadNotification();

        return cardView;
    }

    /**
     *
     */
    private void loadNotification() {
        String notifTitle = notificationExtras.getString(Notification.EXTRA_TITLE);
        String notifText = notificationExtras.getString(Notification.EXTRA_TEXT);
        String notifSubText = notificationExtras.getString(Notification.EXTRA_SUB_TEXT);
        Bitmap notifIcon = notificationExtras.getParcelable(Notification.EXTRA_LARGE_ICON);

        long when = notification.when;

        title.setText(notifTitle);
        content.setText(notifText);
        contentInfo.setText(notifSubText);
        icon.setImageBitmap(notifIcon);
        timestamp.setText(Long.toString(when));
    }

    /**
     *
     * @return
     */
    public Notification getNotification() {
        return notification;
    }
}
