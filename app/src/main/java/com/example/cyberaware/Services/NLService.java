package com.example.cyberaware.Services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.cyberaware.Utils.Constants;
/*
    Notification Listener Service is extended to filter only system and play store updates

    Each event of the service captures the post and removal of these applications
    including if the reason was a negative behaviour

    Updates available, time , package type are all logged to establish the context of the notification.
 */
public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onDestroy() {
        System.out.println("Destroyed");
        super.onDestroy();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Log.i(TAG, "Posted");
        String mTitle;
        String mText;


        String mPackage = sbn.getPackageName();
        mTitle = getTitle(sbn);
        mText = getText(sbn);

        if (isUpdatePackage(mPackage) && isUpdateInTitle(mTitle)) {
            Log.i(TAG, "updates notification");
            Intent i = new Intent("com.example.cyberaware.Services.NLService");
            i.putExtra("id", sbn.getId());
            i.putExtra("time_posted", sbn.getPostTime());
            i.putExtra("package_type", mPackage);
            i.putExtra("event_type", "Posted");
            if(mPackage.equals(Constants.PLAY_STORE_PACKAGE)){
                i.putExtra("updates_available", Integer.parseInt(mText.replaceAll("\\D+","")));
            }
            sendBroadcast(i);
        }
    }

    @Override
    @TargetApi(24)
    public void onListenerConnected() {
        super.onListenerConnected();

        String mTitle;
        String mText;
        Log.i(TAG, "Connected");


        for (StatusBarNotification sbn : getActiveNotifications()) {

            String mPackage = sbn.getPackageName();

            mTitle = getTitle(sbn);
            mText = getText(sbn);

            if (isUpdatePackage(mPackage) && isUpdateInTitle(mTitle)) {
                Log.i(TAG, "Update"+mText);
                Intent i = new Intent("com.example.cyberaware.Services.NLService");
                i.putExtra("id", sbn.getId());
                i.putExtra("time_posted", sbn.getPostTime());
                i.putExtra("package_type", mPackage);
                i.putExtra("event_type", "Connected");
                if(mPackage.equals(Constants.PLAY_STORE_PACKAGE)){
                    i.putExtra("updates_available", Integer.parseInt(mText.replaceAll("\\D+","")));
                }
                sendBroadcast(i);
            }
        }




        Log.i(TAG, "Processed Active Notifications");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
        Log.i(TAG, "Removed");

        String mPackage = sbn.getPackageName();
        String mTitle;

        if (isUpdatePackage(mPackage)) {

            mTitle = getText(sbn);

            if (isUpdateInTitle(mTitle) && isRemovedByUser(reason)) {
                Intent i = new Intent("com.example.cyberaware.Services.NLService");
                i.putExtra("id", sbn.getId());
                i.putExtra("package_type", mPackage);
                i.putExtra("time_removed", System.currentTimeMillis());
                i.putExtra("event_type", "Removed");
                i.putExtra("reason", reason);
                sendBroadcast(i);
            }
        }
    }
    
    private String getTitle(StatusBarNotification sbn) {
        String mTitle = "";
        Bundle mExtras;
        mExtras = sbn.getNotification().extras;

        try {
            if (mExtras.get("android.title") != null) {
                mTitle = String.valueOf(mExtras.get("android.title"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        
        return mTitle;
    }
    
    private String getText(StatusBarNotification sbn) {
        String mText = "";
        Bundle mExtras;
        mExtras = sbn.getNotification().extras;

        try {
            if (mExtras.get("android.text") != null) {
                mText = String.valueOf(mExtras.get("android.text"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        return mText;
    }


    private Boolean isUpdatePackage(String pkg) {
        return (pkg.equals(Constants.SYSTEM_UPDATE_PACKAGE) || pkg.equals(Constants.PLAY_STORE_PACKAGE));
    }

    private Boolean isUpdateInTitle(String title) {
        return (title.contains("updates") || title.contains("System Update"));
    }

    private Boolean isRemovedByUser(int reason) {
        return (reason == REASON_CANCEL || reason == REASON_CANCEL_ALL || reason == REASON_CHANNEL_BANNED
                || reason == REASON_LISTENER_CANCEL || reason == REASON_LISTENER_CANCEL_ALL || reason == REASON_TIMEOUT);
    }
}
