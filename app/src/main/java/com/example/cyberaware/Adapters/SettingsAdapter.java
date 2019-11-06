package com.example.cyberaware.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.cyberaware.R;
import org.apache.commons.text.WordUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
    Temporary adapter class to view the raw data that is captured
 */
public class SettingsAdapter extends BaseAdapter {
    private final ArrayList mData;

    public SettingsAdapter(Map<String, Integer> iMap, Map<String, Boolean> bMap, Map<String, Long> lMap) {
        mData = new ArrayList();
        mData.addAll(convertIntegerMap(iMap));
        mData.addAll(convertBooleanMap(bMap));
        mData.addAll(convertLongMap(lMap));

    }

    public Set<Map.Entry<String,String>> convertIntegerMap(Map<String, Integer> intMap){
        Map<String, String> temp = new HashMap<>();
        for(Map.Entry entry : intMap.entrySet()) {
            temp.put(entry.getKey().toString(),entry.getValue().toString());
        }
        return temp.entrySet();
    }

    public Set<Map.Entry<String,String>> convertBooleanMap(Map<String, Boolean> intMap){
        Map<String, String> temp = new HashMap<>();
        for(Map.Entry entry : intMap.entrySet()) {
            temp.put(entry.getKey().toString(),String.valueOf(entry.getValue()));
        }
        return temp.entrySet();
    }

    public Set<Map.Entry<String,String>> convertLongMap(Map<String, Long> intMap){
        Map<String, String> temp = new HashMap<>();
        for(Map.Entry entry : intMap.entrySet()) {
            temp.put(entry.getKey().toString(), String.valueOf(entry.getValue()));
        }
        return temp.entrySet();
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String,String> getItem(int position) {
;        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private String stripID(String ID) {
        String StripID = ID.replaceAll("[._]"," ");
        StripID = StripID.substring(0,1).toUpperCase() + StripID.substring(1);
        StripID = WordUtils.capitalizeFully(StripID);
        return StripID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_adapter_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(android.R.id.text1)).setText(stripID(item.getKey()));
        ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());
        return result;
    }
}
