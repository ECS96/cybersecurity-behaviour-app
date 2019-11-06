package com.example.cyberaware.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
    Assessment scoring user interface fragment with stubbed score for data for proof of concept purposes
    JSON files are read and outputted to the ui.
 */
public class FragmentAssessment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView listAssessment;

    private TextView totalView, dsView, upView, pgView, paView;

    private ArrayList<HashMap<String, String>> userScore;

    public FragmentAssessment() {

    }

    public static FragmentAssessment newInstance(String param1, String param2) {
        FragmentAssessment fragment = new FragmentAssessment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assessment, container, false);
        loadJSONFromAsset();
        readJSONToArray();

        totalView = view.findViewById(R.id.total);
        dsView = view.findViewById(R.id.ds_value);
        upView = view.findViewById(R.id.up_value);
        paView = view.findViewById(R.id.pa_value);
        pgView = view.findViewById(R.id.pg_value);


        if(!userScore.isEmpty()) {
            HashMap<String, String> score = userScore.get(0);

            String scoreTotal = score.get("total");
            String scoreDS = score.get("ds_value");
            String scoreUp = score.get("up_value");
            String scorePg = score.get("pg_value");
            String scorePa = score.get("pa_value");

            totalView.setText(scoreTotal);
            dsView.setText(scoreDS);
            upView.setText(scoreUp);
            paView.setText(scorePa);
            pgView.setText(scorePg);

        }

        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("userscore.json");
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
            JSONArray m_jArry = obj.getJSONArray("userscore");
            userScore = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String total_value = jo_inside.getString("total");
                String ds_value = jo_inside.getString("device_securement");
                String up_value = jo_inside.getString("updating");
                String pg_value = jo_inside.getString("password_generation");
                String pa_value = jo_inside.getString("proactive_awareness");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("total", total_value);
                m_li.put("ds_value", ds_value);
                m_li.put("up_value", up_value);
                m_li.put("pg_value", pg_value);
                m_li.put("pa_value", pa_value);

                userScore.add(m_li);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
