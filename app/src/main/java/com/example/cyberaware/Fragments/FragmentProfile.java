package com.example.cyberaware.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cyberaware.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/*
    Work in progress user interface fragment for the profile tab
 */
public class FragmentProfile  extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView listProfile;
    private ArrayList<HashMap<String, String>> userInfo;
    private TextView userNameView, userEmailView;

    public FragmentProfile() {

    }

    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        loadJSONFromAsset();
        readJSONToArray();

        userNameView = view.findViewById(R.id.tv_name);
        userEmailView = view.findViewById(R.id.email);


        if(!userInfo.isEmpty()) {
            HashMap<String, String> user = userInfo.get(0);

            String userName = user.get("name");
            String userEmail = user.get("email");

            userNameView.setText(userName);
            userEmailView.setText(userEmail);

        }

        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("userdata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void readJSONToArray() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("userdata");
            userInfo = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("Details-->", jo_inside.getString("name"));
                String name_value = jo_inside.getString("name");
                String email_value = jo_inside.getString("email");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("name", name_value);
                m_li.put("email", email_value);

                userInfo.add(m_li);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
