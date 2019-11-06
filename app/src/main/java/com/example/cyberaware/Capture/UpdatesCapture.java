package com.example.cyberaware.Capture;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;

import com.example.cyberaware.Utils.Constants;

import java.util.ArrayList;
/*
    Update class is responsible for all notification data about updating the system and applications

    All variables are self explanatory for all information captured within a notification

    A  Receiver handles the raw data from the service and filters the behaviours
 */
public class UpdatesCapture {


    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private static int mSoftwareUpdateDismissCount;

    private static int mPlayStoreDismissCount;

    private static int mSoftwareUpdateNoteCount;
    private static int mPlayStoreNoteCount;
    
    private static int mPlayStoreUpdates;

    private static long mSoftwareUpdateRespTimer;
    private static long mPlayStoreRespTimer;

    private static ArrayList<Reason> mReasonList = new ArrayList<>();
    
    private static Activity mActivity;
    private static ContentResolver mContentResolver;
    public static NotificationReceiver mNotificationReceiver;

    private static ArrayList<NotificationInfo> mNoteList = new ArrayList<>();

    //private static PackageManager mPackageManager;


    /*
        Initialise Class Data
        Register Receiver If app has no permission start intent to change it
     */
    public static void init(Activity activity){

        mActivity = activity;
        mContentResolver = mActivity.getContentResolver();

        if(isNotificationServiceEnabled()) {
            mNotificationReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.cyberaware.Services.NLService");
            mActivity.registerReceiver(mNotificationReceiver, filter);
        } else{
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            mActivity.startActivity(intent);
        }


        mSoftwareUpdateDismissCount = 0;
        mPlayStoreDismissCount = 0;

        mSoftwareUpdateNoteCount = 0;
        mPlayStoreNoteCount = 0;

        mPlayStoreUpdates = 0;

        mSoftwareUpdateRespTimer = 0;
        mPlayStoreRespTimer = 0;

    }

    /*
        Checks the list of accessibilty listeners for this package
     */
    private static boolean isNotificationServiceEnabled(){
        String mPkgName = mActivity.getPackageName();
        final String flat = Settings.Secure.getString(mContentResolver, ENABLED_NOTIFICATION_LISTENERS);
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

    /*
        Only works for some packages version numbers are not publicly available
     */
    /*
    private static void getPackageSettings(){
        List<ApplicationInfo> packages =  mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo pInfo : packages) {
            if (mPackageManager.getLaunchIntentForPackage(pInfo.packageName) == null) {
                packages.remove(pInfo);
            }
        }

        for (ApplicationInfo packageInfo : packages) {
                    packageNames.add(packageInfo.packageName);
                    VersionChecker vC = new VersionChecker();
                    vC.setPackageName(packageInfo.packageName);
                    try {
                        PackageInfo pInfo = mPackageManager.getPackageInfo(packageInfo.packageName, 0);
                        String latestVersion = vC.execute().get();
                        packageVersions.add(pInfo.versionName + " " + latestVersion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        System.out.println("Package Names: "+packageNames);
        System.out.println("Package Versions: "+packageVersions);
    }
    */

    /*
        Notification Receiver process the data sent by Notification Listener Service
        To filter the data and store the behaviour values that can be derived from it
        Three types of event posted, removed and connected handle all states of notification
     */
    static class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long mTimePosted, mTimeRemoved;
            int mID, mReason, mUpdatesAvailable = 0;
            String mEventType, mPackageType;

            if(mNoteList == null){
                mNoteList = new ArrayList<>();
            }

            mID = intent.getIntExtra("id", 0);
            mEventType = intent.getStringExtra("event_type");
            mPackageType = intent.getStringExtra("package_type");

            NotificationInfo note = new NotificationInfo();
            note.id = mID;
            note.packageType = mPackageType;

            if(mEventType.equals("Removed")) {
                mTimeRemoved = intent.getLongExtra("time_removed", 1l);
                mReason = intent.getIntExtra("reason", 0);
                notificationRemoved(note, mReason, mTimeRemoved);
            }
            if(mEventType.equals("Posted")) {
                mTimePosted = intent.getLongExtra("time_posted", 1l);
                if(mPackageType.equals(Constants.PLAY_STORE_PACKAGE)){
                    mUpdatesAvailable = intent.getIntExtra("updates_available", 0);
                }
                notificationPosted(note, mTimePosted, mUpdatesAvailable);
            }

            if(mEventType.equals("Connected")) {
                mTimePosted = intent.getLongExtra("time_posted", 1l);
                if(mPackageType.equals(Constants.PLAY_STORE_PACKAGE)) {
                    mUpdatesAvailable = intent.getIntExtra("updates_available", 0);
                }
                notificationConnected(note, mTimePosted, mUpdatesAvailable);
            }
        }
        /*
            Posted updates calculate time, id, count for each type of update
         */
        private void notificationPosted(NotificationInfo mNote, long mTimePosted, int mUpdatesAvailable){
            if(mNoteList.contains(mNote.packageType)){
                for(NotificationInfo note : mNoteList) {
                    if (note.packageType.equals(mNote.packageType)) {
                        if (note.id != mNote.id) {
                            note.id = mNote.id;
                        }
                    }
                }
            } else {
                mNote.timePosted = mTimePosted;
                mNoteList.add(mNote);
            }

            if (mNote.packageType.equals(Constants.PLAY_STORE_PACKAGE)) {
                mPlayStoreNoteCount++;
                mPlayStoreUpdates =  mUpdatesAvailable;
            }

            if(mNote.packageType.equals(Constants.SYSTEM_UPDATE_PACKAGE)){
                mSoftwareUpdateNoteCount++;
            }
        }

        /*
            Everything that posted does but includes a reason for the dismissal
            Each reason is counted for separately
         */
        private void notificationRemoved(NotificationInfo mNote, int mReason, long mTimeRemoved) {
            long mTimePosted;

            Reason mNewReason = new Reason();
            mNewReason.reason = mReason;
            mNewReason.count = 1;
            
            if(!mReasonList.isEmpty()){
                for (Reason r : mReasonList){
                    if(r.reason == mNewReason.reason){
                        r.count++;
                        break;
                    }
                }
                mReasonList.add(mNewReason);
            } else {
                mReasonList.add(mNewReason);
            }

            if (mNoteList.contains(mNote.packageType)) {
                for (NotificationInfo note : mNoteList) {
                    if (note.packageType.equals(mNote.packageType)
                            && note.id == mNote.id) {
                        mNoteList.remove(note);
                        mTimePosted = note.timePosted;
                        if (mNote.equals(Constants.PLAY_STORE_PACKAGE)) {
                            mPlayStoreDismissCount++;
                            mPlayStoreRespTimer += (mTimePosted - mTimeRemoved);
                        } else {
                            mSoftwareUpdateRespTimer += (mTimePosted - mTimeRemoved);
                            mSoftwareUpdateDismissCount++;
                        }

                    }
                }
            }
        }

        /*
            All notifications collected which were posted before service was started
         */
        private void notificationConnected(NotificationInfo mNote, long mTimePosted, int mUpdatesAvailable){
            if(mNoteList.contains(mNote.packageType)){
                for(NotificationInfo note : mNoteList) {
                    if (note.packageType.equals(mNote.packageType)
                            && note.id != mNote.id) {
                        note.id = mNote.id;
                    }
                }
            } else {
                mNote.timePosted =  mTimePosted;
                mNoteList.add(mNote);
            }

            if (mNote.packageType.equals(Constants.PLAY_STORE_PACKAGE)) {
                mPlayStoreNoteCount++;
                mPlayStoreUpdates = mUpdatesAvailable;
            }

            if(mNote.packageType.equals(Constants.SYSTEM_UPDATE_PACKAGE)){
                mSoftwareUpdateNoteCount++;
            }
        }

    }
    /*
        All get method for Data.
     */
    public static int getSoftwareUpdateDismissCount() {
        return mSoftwareUpdateDismissCount;
    }

    public static int getPlayStoreDismissCount() {
        return mPlayStoreDismissCount;
    }

    public static int getSoftwareUpdateNoteCount() {
        return mSoftwareUpdateNoteCount;
    }

    public static int getPlayStoreNoteCount() {
        return mPlayStoreNoteCount;
    }

    public static int getPlayStoreUpdates() {
        return mPlayStoreUpdates;
    }

    public static long getSoftwareUpdateRespTimer() {
        return mSoftwareUpdateRespTimer;
    }

    public static long getPlayStoreRespTimer() {
        return mPlayStoreRespTimer;
    }

    public static ArrayList<Reason> getReasonList() {
        return mReasonList;
    }

    /*
        Reason class stores reason data of notifications being removed
     */
    public static class Reason{
        private int reason;
        private int count;

        public int getReason(){
            return reason;
        }

        public int getCount(){
            return count;
        }
    }

    
    /*
        Notification info class stores information about the notifications for handling
        multiple notifications of the system and data tracking over time
     */
    private static class NotificationInfo{
        private int id;
        private String packageType;
        private long timePosted;
    }
}
