package com.example.cyberaware.Capture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.example.cyberaware.Capture.PasswordData.PasswordCapture;
import com.example.cyberaware.Utils.Constants;

import java.util.HashMap;
import java.util.Map;
/*
    Handles the data processing by running multiple threads that analyse and retrieve data on the fly in the background of the smartphone

    Handlers and Runnables are used to run methods and update the frequency of processing

    getData - logs all the data captured in the application for debugging and results

    unregisterreceivers - shuts down the background processes so they do not leak data.
 */
public class DataHandler {

    private static String TAG = "DataHandler: ";

    public static Map<String, Boolean> mBooleanList = new HashMap<>();
    public static Map<String, Integer> mIntegerList = new HashMap<>();
    public static Map<String, Long> mTimerList = new HashMap<>();
    public static Map<String, String> mStringList = new HashMap<>();


    private static Handler mTimerHandler;
    private static Runnable mTimerRunnable;

    private static Handler mStaticHandler;
    private static Runnable mStaticRunnable;

    private static Handler mSettingsHandler;
    private static Runnable mSettingsRunnable;


    private static Activity mActivity;


    @TargetApi(26)
    public static void init(Activity activity) {
        mTimerHandler = new Handler();
        mStaticHandler = new Handler();
        mSettingsHandler = new Handler();

        mActivity = activity;

        DeviceSecureCapture.init(mActivity);
        PasswordCapture.init(mActivity);
        UpdatesCapture.init(mActivity);
        AndroidSettingsCapture.init(mActivity);

        updateCapture();
        updateLog();

        updateSettings();
        
        //getSystemSettings();
        //writeJSON();
        //update();
    }

    private static void updateCapture(){
        final long mCaptureUpdateTimer = 1000;
        mTimerHandler.postDelayed(mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimerValues();
                updateTimers();
                mTimerHandler.postDelayed(mTimerRunnable, mCaptureUpdateTimer);
            }
        }, mCaptureUpdateTimer);
    }

    private static void updateLog(){
        final long mLogUpdateTimer = 5000;
        mStaticHandler.postDelayed(mStaticRunnable = new Runnable() {
            @Override
            public void run() {
                updateValues();
                getData();
                mStaticHandler.postDelayed(mStaticRunnable, mLogUpdateTimer);
            }
        }, mLogUpdateTimer);
    }

    private static void updateSettings(){
        final long mLogUpdateTimer = 60000;
        mSettingsHandler.postDelayed(mSettingsRunnable = new Runnable() {
            @Override
            public void run() {
                getSystemData();
                mSettingsHandler.postDelayed(mSettingsRunnable, mLogUpdateTimer);
            }
        }, mLogUpdateTimer);
    }



    private static void updateValues(){
        AndroidSettingsCapture.updateStaticNetworkValues();
    }

    public static void updateTimerValues(){
        DeviceSecureCapture.updateValues();
        AndroidSettingsCapture.updateNetworkValues();
    }

    public static void updateTimers() {
        DeviceSecureCapture.updateTimer();
        AndroidSettingsCapture.updateWifiTimer();
    }
    
    public static void getData() {
        String logInfo;

        //Device Securement Behaviours

        logInfo = TAG + "Device Securement: ";
        Log.i(logInfo + Constants.SCREEN_LOCKS, String.valueOf(DeviceSecureCapture.getScreenLocks()));
        Log.i(logInfo + Constants.SCREEN_UNLOCKS, String.valueOf(DeviceSecureCapture.getScreenUnlocks()));
        Log.i(logInfo + Constants.SECURED, String.valueOf(DeviceSecureCapture.getSecured()));
        Log.i(logInfo + Constants.UNLOCK_TIMER, String.valueOf(DeviceSecureCapture.getUnlockTimer()));

        //Password Generation Behaviours

        logInfo = TAG + "Password Generation: ";
        for (PasswordCapture.Password t : PasswordCapture.getPassList()) {
            Log.i(logInfo + Constants.HASHED_PASS, t.getPass());
            Log.i(logInfo + Constants.PASS_COUNT, String.valueOf(t.getCount()));
            Log.i(logInfo + Constants.PASS_STRENGTH, String.valueOf(t.getStrength()));
        }

        //Update Behaviour
        logInfo = TAG + "Updating: ";

        Log.i(logInfo + Constants.PS_NOT_CNT, String.valueOf(UpdatesCapture.getPlayStoreNoteCount()));
        Log.i(logInfo + Constants.PS_DIS_CNT, String.valueOf(UpdatesCapture.getPlayStoreDismissCount()));
        Log.i(logInfo + Constants.PS_UPDATES, String.valueOf(UpdatesCapture.getPlayStoreUpdates()));
        Log.i(logInfo + Constants.PS_NOT_RESP_TIMER, String.valueOf(UpdatesCapture.getPlayStoreRespTimer()));

        Log.i(logInfo + Constants.SU_NOT_CNT, String.valueOf(UpdatesCapture.getSoftwareUpdateNoteCount()));
        Log.i(logInfo + Constants.SU_DIS_CNT, String.valueOf(UpdatesCapture.getSoftwareUpdateDismissCount()));
        Log.i(logInfo + Constants.SU_NOT_RESP_TIMER, String.valueOf(UpdatesCapture.getSoftwareUpdateRespTimer()));

        for (UpdatesCapture.Reason r : UpdatesCapture.getReasonList()) {
            Log.i(logInfo + Constants.REASON, String.valueOf(r.getReason()));
            Log.i(logInfo + Constants.REASON_CNT, String.valueOf(r.getCount()));
        }


    }

    public static void getSystemData(){

        String logInfo;

        logInfo = TAG + "Connectivity Timers: ";
        for (Map.Entry entry : AndroidSettingsCapture.getTimerList().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));
        }

        logInfo = TAG + "Network Boolean: ";
        for (Map.Entry entry : AndroidSettingsCapture.getBooleanList().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));
        }

        logInfo = TAG + "Integer Settings: ";
        for (Map.Entry entry : AndroidSettingsCapture.getProviderList().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));

        }

        logInfo = TAG + "Provider Settings: ";
        for (Map.Entry entry : AndroidSettingsCapture.getIntegerList().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));

        }

        logInfo = TAG + "Global Settings: ";
        for (Map.Entry entry : AndroidSettingsCapture.getGlobalSettings().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));
        }
        logInfo = TAG + "Secure Settings: ";
        for (Map.Entry entry : AndroidSettingsCapture.getSecureSettings().entrySet()) {
            Log.i(logInfo + entry.getKey() + " ", String.valueOf(entry.getValue()));
        }

    }

    public static void unregisterReceivers(){

        mActivity.unregisterReceiver(UpdatesCapture.mNotificationReceiver);
        mActivity.unregisterReceiver(PasswordCapture.mAccessibilityReceiver);
        mActivity.unregisterReceiver(DeviceSecureCapture.getLockReceiver());
    }

}