package com.example.cyberaware.Capture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.example.cyberaware.Utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/*
        All behaviours outside SeBIS are captured here as extras
        Connectivity
        System Settings
        Misc. Settings
     */

public class AndroidSettingsCapture {

    private static Map<String,Integer> mGlobalSettings = new HashMap<>();
    private static Map<String,Integer> mSecureSettings = new HashMap<>();
    private static Map<String,Integer> mProviderList = new HashMap<>();

    private static ArrayList<String> mGlobalList = new ArrayList(Arrays.asList(Settings.Global.AIRPLANE_MODE_ON,Settings.Global.BLUETOOTH_ON, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, Settings.Global.WIFI_ON, Settings.Global.USB_MASS_STORAGE_ENABLED,
            Settings.Global.DATA_ROAMING, Settings.Global.BOOT_COUNT, Settings.Global.NETWORK_PREFERENCE, Settings.Global.NETWORK_PREFERENCE));

    private static ArrayList<String> mSecureList = new ArrayList<>(Arrays.asList(Settings.Secure.ACCESSIBILITY_ENABLED, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, Settings.Secure.TOUCH_EXPLORATION_ENABLED, Settings.Secure.PARENTAL_CONTROL_ENABLED,
            Settings.Secure.ALLOWED_GEOLOCATION_ORIGINS));

    private static boolean mLocationEnabled;
    private static boolean mUnknownAppSourcesEnabled;


    public static Map<String, Boolean> mBooleanList = new HashMap<>();
    public static Map<String, Integer> mIntegerList = new HashMap<>();
    public static Map<String, Long> mTimerList = new HashMap<>();
    
    private static Activity mActivity;
    private static PackageManager mPackageManager;
    private static WifiManager mWifiManager;
    private static LocationManager mLocationManager;


    private static Boolean mWifiConn, mWifiOn, mBTConn, mBTOn;

    
    public static void init(Activity activity){

        mActivity = activity;
        mPackageManager = mActivity.getPackageManager();
        mWifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Activity.WIFI_SERVICE);
        mLocationManager = (LocationManager) mActivity.getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);
        mUnknownAppSourcesEnabled = mPackageManager.canRequestPackageInstalls();
        mBooleanList.put("Unknown App Sources Enabled",mUnknownAppSourcesEnabled);
        mTimerList.put(Constants.WIFI_ON_TIMER_S, -1l);
        mTimerList.put(Constants.WIFI_ON_TIMER_D, 0l);

        mTimerList.put(Constants.BT_ON_TIMER_S, -1l);
        mTimerList.put(Constants.BT_ON_TIMER_D, 0l);
        
        getSystemSettings();
        getLocationSettings();

    }


    public static Map<String, Integer> getGlobalSettings() {
        return mGlobalSettings;
    }

    public static Map<String, Integer> getIntegerList() {
        return mIntegerList;
    }

    public static Map<String, Integer> getSecureSettings() {
        return mSecureSettings;
    }

    public static Map<String, Long> getTimerList() {
        return mTimerList;
    }

    public static Map<String, Boolean> getBooleanList() {
        return mBooleanList;
    }

    public static Map<String, Integer> getProviderList() {
        return mProviderList;
    }

    public static boolean isLocationEnabled() {
        return mLocationEnabled;
    }

    public static boolean isUnknownAppSourcesEnabled() {
        return mUnknownAppSourcesEnabled;
    }
    /*
        WiFi timer for on and disconnected
        Bluetooth timer for on and disconnected
     */
    public static void updateWifiTimer(){
        if(mBooleanList.get(Constants.WIFI_ON) && !mBooleanList.get(Constants.WIFI_CONNECTED) && mTimerList.get(Constants.WIFI_ON_TIMER_S) == -1l){
            mTimerList.put(Constants.WIFI_ON_TIMER_S, System.currentTimeMillis());
        } else {
            if (mTimerList.get(Constants.WIFI_ON_TIMER_S) != -1l) {
                mTimerList.put(Constants.WIFI_ON_TIMER_D, mTimerList.get(Constants.WIFI_ON_TIMER_D) + (System.currentTimeMillis() - mTimerList.get(Constants.WIFI_ON_TIMER_S)));
                mTimerList.put(Constants.WIFI_ON_TIMER_S, -1l);
            }
        }

        if(mBooleanList.get(Constants.BT_ON) && !mBooleanList.get(Constants.BT_CONNECTED) && mTimerList.get(Constants.BT_ON_TIMER_S) == -1l){
            mTimerList.put(Constants.BT_ON_TIMER_S, System.currentTimeMillis());
        } else {
            if (mTimerList.get(Constants.BT_ON_TIMER_S) != -1l) {
                mTimerList.put(Constants.BT_ON_TIMER_D, mTimerList.get(Constants.BT_ON_TIMER_D) + (System.currentTimeMillis() - mTimerList.get(Constants.BT_ON_TIMER_S)));
                mTimerList.put(Constants.BT_ON_TIMER_S, -1l);
            }
        }
    }
    public static void updateNetworkValues(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mWifiConn = mWifiManager.getConnectionInfo().getSupplicantState().equals("COMPLETED");

        mWifiOn = mWifiManager.isWifiEnabled();

        mBTOn = mBluetoothAdapter.isEnabled();

        mBTConn = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP)  != BluetoothProfile.STATE_DISCONNECTED
                && mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) != BluetoothProfile.STATE_DISCONNECTED;

        mBooleanList.put(Constants.WIFI_CONNECTED, mWifiConn);
        mBooleanList.put(Constants.WIFI_ON, mWifiOn);
        mBooleanList.put(Constants.BT_CONNECTED, mBTConn);
        mBooleanList.put(Constants.BT_ON, mBTOn);
    }

    public static void updateStaticNetworkValues(){
        BitSet mOpenNet;
        int mCount;

        mOpenNet = new BitSet();
        mOpenNet.set(WifiConfiguration.KeyMgmt.NONE, true);

        mCount = 0;
        for (WifiConfiguration wc : mWifiManager.getConfiguredNetworks()){
            if(wc.allowedKeyManagement.equals(mOpenNet)){
                mCount++;
            }
        }
        mIntegerList.put("Unsecured Networks",mCount);
    }

    @TargetApi(24)
    private static void getSystemSettings(){

        for (String setting : mGlobalList) {
            try {
                mGlobalSettings.put(setting, Settings.Global.getInt(mActivity.getContentResolver(), setting));
            } catch (Settings.SettingNotFoundException e) {
                //e.printStackTrace();
            }
        }

        for (String setting : mSecureList) {
            try {
                mSecureSettings.put(setting, Settings.Secure.getInt(mActivity.getContentResolver(), setting));
            } catch (Settings.SettingNotFoundException e) {
                //e.printStackTrace();
            }
        }

    }

    @TargetApi(28)
    private static void getLocationSettings(){

        mLocationEnabled = mLocationManager.isLocationEnabled(); // 28

        for (String provider : mLocationManager.getAllProviders()) {
            mProviderList.put(provider , mLocationManager.isProviderEnabled(provider)? 1 : 0);
        }
    }
}
