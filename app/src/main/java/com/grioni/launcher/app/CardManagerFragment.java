package com.grioni.launcher.app;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matias Grioni on 8/30/14.
 */
public class CardManagerFragment extends Fragment {

    private List<CardFragment> cards;

    private View containerView;
    private int containerResourceId;
    private int notificationLayoutId;

    private NotificationReceiver notificationReceiver;

    /**
     *
     * @param containerResourceId
     * @return
     */
    public static CardManagerFragment newInstance(int containerResourceId, int notificationLayoutId) {
        CardManagerFragment cardManager = new CardManagerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt("containerResourceId", containerResourceId);
        arguments.putInt("notificationLayoutId", notificationLayoutId);

        cardManager.setArguments(arguments);

        return cardManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        containerResourceId = arguments.getInt("containerResourceId");
        notificationLayoutId = arguments.getInt("notificationLayoutId");

        cards = new ArrayList<CardFragment>();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(notificationReceiver == null)
            notificationReceiver = new NotificationReceiver();
        getActivity().registerReceiver(notificationReceiver,
                new IntentFilter(MainActivity.INTENT_ACTION_NOTIFICATION_POSTED));
        getActivity().registerReceiver(notificationReceiver,
                new IntentFilter(MainActivity.INTENT_ACTION_NOTIFICATION_REMOVED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        notificationReceiver = new NotificationReceiver();
        containerView = inflater.inflate(containerResourceId, container, false);

        return containerView;
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(notificationReceiver);
    }

    /**
     *
     * @param card
     */
    private void addCard(CardFragment card) {
        cards.add(card);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.card_manager_container, card);

        transaction.commit();
    }

    /**
     *
     * @param notification
     * @return
     */
    public int getNotificationIndex(Notification notification) {
        for(int i = 0; i < cards.size(); i++) {
            CardFragment cardFragment = cards.get(i);

            if(cardFragment instanceof NotificationCardFragment) {
                Notification target = ((NotificationCardFragment) cardFragment).getNotification();
                if(target.equals(notification))
                    return i;
            }
        }

        return -1;
    }

    /**
     *
     */
    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Notification notification = extras.getParcelable("notification");

            if(intent.getAction() == MainActivity.INTENT_ACTION_NOTIFICATION_POSTED) {
                NotificationCardFragment newCard = NotificationCardFragment
                        .newInstance(notificationLayoutId, notification);
                addCard(newCard);
            } else if(intent.getAction() == MainActivity.INTENT_ACTION_NOTIFICATION_REMOVED) {
                if(getNotificationIndex(notification) != -1) {

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.remove(cards.remove(getNotificationIndex(notification)));

                    transaction.commit();
                }
            }
        }
    }
}
