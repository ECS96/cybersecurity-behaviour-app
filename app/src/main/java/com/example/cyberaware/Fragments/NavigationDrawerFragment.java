package com.example.cyberaware.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.cyberaware.Adapters.DrawerAdapter;
import com.example.cyberaware.R;
import com.example.cyberaware.Adapters.Label;

import java.util.ArrayList;
import java.util.List;

/*
    Fragment for the Navigation Drawer
    Opted for the Tabbed Layout so class is redundant but left for proof of work
 */
public class NavigationDrawerFragment extends Fragment  {

    private RecyclerView recyclerView;

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private DrawerAdapter adapter;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private View containerView;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer =  Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER,"false"));
        if(savedInstanceState!= null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = layout.findViewById(R.id.drawerList);

        adapter = new DrawerAdapter(getActivity(), getData());
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView,new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*
                if(position==0) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                if(position==1){
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                }
                if(position==2) {
                    startActivity(new Intent(getActivity(), FeedbackActivity.class));
                }
                if(position == 3) {
                    startActivity(new Intent(getActivity(), FeedbackActivity.class));
                }
                */
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }

    public static List<Label> getData(){
        List<Label> data = new ArrayList<>();
        int[] icons = {R.drawable.ic_home,R.drawable.ic_assessment,R.drawable.ic_feedback,R.drawable.ic_settings_applications};
        String[] titles = {"Home", "Profile", "Feedback", "Settings"};

        for(int i=0; i< titles.length && i <icons.length; i++){
            Label current =  new Label();
            current.iconId = icons[i];
            current.title = titles[i];

            data.add(current);
        }

        return data;

    }

    public void setUp(int fragmentId, final DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                if(slideOffset <0.6) {
                    toolbar.setAlpha(1 - slideOffset);
                }

            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.commit();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;


        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if(child!= null && clickListener != null){
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                    super.onLongPress(e);
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!= null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }

            gestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
