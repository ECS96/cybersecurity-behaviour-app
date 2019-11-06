package com.example.cyberaware.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cyberaware.Capture.DataHandler;
import com.example.cyberaware.R;
import com.example.cyberaware.Adapters.SettingsAdapter;
/*
    Work in progress user interface fragment for the fragment tab
    Displays raw data from the behaviour capture classes adapted using SettingsAdapter
 */
public class FragmentFeedback  extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView listFeedback;

    private TextView textView;

    public FragmentFeedback() {

    }

    public static FragmentFeedback newInstance(String param1, String param2) {
        FragmentFeedback fragment = new FragmentFeedback();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        listFeedback = view.findViewById(R.id.listFeedback);

        SettingsAdapter adapter = new SettingsAdapter(DataHandler.mIntegerList, DataHandler.mBooleanList, DataHandler.mTimerList);

        listFeedback.setAdapter(adapter);

        return view;
    }

}
