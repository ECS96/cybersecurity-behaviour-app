package com.example.cyberaware.Utils;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DataIO {

    private static JSONObject mJSONObject;

    public static void saveData(){

    }

    private static JSONObject createJSON(Map<String, Integer> mGlobalSettings) {

        JSONArray data = new JSONArray();
        JSONObject dataEntry = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String currentDT = sdf.format(new Date());

        try {
            dataEntry.put("Date", currentDT);
            for (Map.Entry i : mGlobalSettings.entrySet()) {
                dataEntry.put(i.getKey().toString(), i.getValue());
            }
            mJSONObject = new JSONObject();
            data.put(dataEntry);
            mJSONObject.put("Data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mJSONObject;
    }

    public static void writeJSON(Activity mActivity,String fileName, Map<String, Integer> mGlobalSettings){
        createJSON(mGlobalSettings);

        File file =  new File(mActivity.getFilesDir(),fileName);
        if(!file.exists()){
            file.mkdir();
        }
        try{
            File rawDataFile = new File(file, "rawdata.json");
            FileWriter writer = new FileWriter(rawDataFile);
            writer.append(mJSONObject.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
