package com.example.cyberaware.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.cyberaware.Capture.PasswordData.Hash;
import com.example.cyberaware.Capture.PasswordData.PasswordCapture;

import java.util.Map;
/*
    Accessibility Service is extended to filter the event of entering your password
    Each password that is inputted and correct is build character by character and sent to be
    hashed.
 */
public class ABService extends AccessibilityService {
    private static final String TAG = AccessibilityService.class.getSimpleName();
    String mString = "";
    String mPassword = "";
    String mPackage = "";
    String mAnnouncement = "";
    private PasswordCapture.AccessibilityReceiver mAccessibilityReceiver;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.v(TAG, String.format(
                    "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s [password] %s",
                    getEventType(event), event.getClassName(), event.getPackageName(),
                    event.getEventTime(), getEventText(event), event.isPassword()));


        if(hasStartedPassword(event)) {

            CharSequence pass = event.getText().get(0);

            if(pass.length()>0) {
                Character lastChar = pass.charAt(pass.length() - 1);
                if(lastChar.equals('•') && mString.length()>0) {
                    mString = mString.substring(0,mString.length()-1);
                } else {
                    mString += lastChar;
                }
            } else {
                mString = "";
            }
        } else if(hasFinishedPassword(event)){
            mPassword = mString;
            mPackage = (String)event.getPackageName();
            mString = "";
        }


        if(event.getEventType() == AccessibilityEvent.TYPE_ANNOUNCEMENT){

            if(event.getText() != null && event.getText().size()>0) {
                mAnnouncement = event.getText().get(0).toString();
            } else {
                mAnnouncement ="";
            }
            if(mAnnouncement.contains("Wrong") || mAnnouncement.contains("Incorrect")){
                mPassword = "";
            } else if(!mPassword.equals("")){

                Map.Entry<String,Map.Entry<Integer,Integer>> passStat = Hash.hashPass(mPassword, mPackage);
                mPassword = "";

                Intent i =  new Intent("com.example.cyberaware.Services.ABService");
                i.putExtra("pass", passStat.getKey());
                i.putExtra("count", passStat.getValue().getKey());
                i.putExtra("strength", passStat.getValue().getValue());
                sendBroadcast(i);
            }
        }

    }

    private Boolean hasStartedPassword(AccessibilityEvent event){
        return (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
                && event.isPassword()
                && event.getText() != null);
    }

    private Boolean hasFinishedPassword(AccessibilityEvent event){
        return event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED && !event.isPassword();
    }

    private Boolean hasStartedSMPassword(AccessibilityEvent event){
        return (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
                && event.isPassword()
                && event.getText() != null);
    }

    private Boolean hasFinishedSMPassword(AccessibilityEvent event) {
        return (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.getText().contains("Facebook")) ||
                (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.getText().contains("Instagram"));

    }


    private String getEventText(AccessibilityEvent event) {



        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }


        return sb.toString();
    }

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
        }
        return String.valueOf(event.getEventType());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAccessibilityReceiver);
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
}

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 0;
        info.packageNames = null;
        setServiceInfo(info);

        mAccessibilityReceiver = new PasswordCapture.AccessibilityReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("list");
        registerReceiver(mAccessibilityReceiver, filter);
    }
}
