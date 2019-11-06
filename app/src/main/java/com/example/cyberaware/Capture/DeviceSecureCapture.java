package com.example.cyberaware.Capture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.view.Display;

/*
    @class Device Secure Capture - captures all device security behaviours
    - Timer device was left unlocked
    - Screen Unlocks
    - Screen Locks
    - Device Security enabled
    @author Edwin Stephens
    @version 1.0.0
 */

public class DeviceSecureCapture {

    //Conditional Variables
    private static Boolean mDisplayOff, mLocked, mSecure;

    //Managers for Accessing System Resources
    private static DisplayManager mDisplayManager;
    private static KeyguardManager mKeyguardManager;

    //Timer variables
    private static long mTimerStart;
    private static long mTimerDuration;

    //Counts for Locking Behaviour
    private static int mScreenLocks;
    private static int mScreenUnlocks;

    //Receiver for Locking Actions
    private static LockScreenStateReceiver mLockScreenStateReceiver;

    @TargetApi(23)
    public static void init(Activity mActivity){

        //Initialise System Managers with System Services of Android Device
        mDisplayManager = (DisplayManager) mActivity.getSystemService(Activity.DISPLAY_SERVICE);
        mKeyguardManager = (KeyguardManager) mActivity.getSystemService(Activity.KEYGUARD_SERVICE);

        //init lock counters
        mScreenUnlocks = 0;
        mScreenLocks = 0;

        // timer variables
        mTimerStart = -1l;
        mTimerDuration = 0;

        //Register a receiver for handling the actions screen on or off to determine the locking behaviour
        mLockScreenStateReceiver = new LockScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mActivity.registerReceiver(mLockScreenStateReceiver, filter);
    }

    /*
        Updates conditional values of device security.
     */
    public static void updateValues(){

        mDisplayOff = mDisplayManager.getDisplay(0).getState()== Display.STATE_OFF;
        mLocked = mKeyguardManager.isDeviceLocked();
        mSecure = mKeyguardManager.isDeviceSecure();


    }

    /*
        Unlock Timer
        If the device is unsecure and the timer has not been started set to the current time.
        If the timer has started and it is still unsecure it does nothing
        If the timer has started and it is secure it adds the total time between the start and current time
        to the total duration and resets the timer
     */
    public static void updateTimer(){
        if(isUnsecure() && mTimerStart == -1l) {
            mTimerStart = System.currentTimeMillis();
        } else{
            if(mTimerStart != -1l){
                mTimerDuration +=  (System.currentTimeMillis() - mTimerStart);
                mTimerStart = -1l;
            }
        }

    }
    /*
        @returns When the phone screen is off and device is not locked
     */
    private static boolean isUnsecure(){
        return (mDisplayOff && !mLocked);
    }

    /*
        @returns Retrieves receiver so it can be unregistered when application is destroyed
     */
    public static LockScreenStateReceiver getLockReceiver(){
        return  mLockScreenStateReceiver;
    }
    /*
        @Returns the unlock the timer
     */
    public static Long getUnlockTimer(){
        return mTimerDuration;
    }
    /*
        Returns the screen locks
     */
    public static int getScreenLocks(){
        return  mScreenLocks;
    }
    /*
        Returns the screen unlocks
     */
    public static int getScreenUnlocks(){
        return mScreenUnlocks;
    }

    /*
        Returns if device security is setup
     */
    public static Boolean getSecured(){
        return mSecure;
    }

    /*
        Receiver Class - For locking behaviour
        If the phone is actively being unlocked or locked by the user - Power Button
        An intent sent by the system is filtered to the action of the user
        If the action was on or off increase the unlock or lock of the phone
        Handles the transition from the screen being on or off to the locking and unlocking
        action with delays and rechecks.
     */
    private static class LockScreenStateReceiver extends BroadcastReceiver {
        long passwordDelay = 5000; //Set to time it takes for a user to enter password after screen on
        long lockWindow = 2000; //Set to time for lock to activate
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    try {
                        if(!mLocked) {
                            Thread.sleep(lockWindow); //Wait for lock window
                            updateValues();
                            if (mLocked) {
                                mScreenLocks++;
                            }
                        }
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                try {
                    updateValues();
                    if(mLocked) {
                        Thread.sleep(passwordDelay);
                        updateValues();
                        if (!mLocked) {
                            mScreenUnlocks++;
                        }
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
