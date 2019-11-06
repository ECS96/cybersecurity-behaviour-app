package com.example.cyberaware.Capture.PasswordData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;
/*
    Captures raw passwords sent by the accessiblity service

    Stores the count and strength of each pass

    Checks if the accessibility service is enabled.
 */
public class PasswordCapture {

    private static Activity mActivity;
    private static ContentResolver mContentResolver;
    public static AccessibilityReceiver mAccessibilityReceiver;

    private static ArrayList<Password> mPassList = new ArrayList<>();

    public static void init(Activity activity){
        mActivity = activity;
        mContentResolver = mActivity.getContentResolver();

        if(isAccessibilityServiceEnabled()) {
            mAccessibilityReceiver = new AccessibilityReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.cyberaware.Services.ABService");
            mActivity.registerReceiver(mAccessibilityReceiver, filter);
        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            mActivity.startActivityForResult(intent, 0);
        }
    }

    public static ArrayList<Password> getPassList(){
        return mPassList;
    }

    private static boolean isAccessibilityServiceEnabled(){
        String mPkgName = mActivity.getPackageName();
        final String flat = Settings.Secure.getString(mContentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(mPkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static class AccessibilityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Password passData = new Password();
            passData.pass = intent.getStringExtra("pass");
            passData.count = intent.getIntExtra("count",0 );
            passData.strength = intent.getIntExtra("strength", 0);

            mPassList.add(passData);
        }
    }

    public static class Password{
        private String pass;
        private int count;
        private int strength;

        public String getPass(){
            return pass;
        }

        public int getCount(){
            return count;
        }

        public int getStrength(){
            return strength;
        }
    }
}
