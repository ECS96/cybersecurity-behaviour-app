package com.example.cyberaware.Activities;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cyberaware.Capture.DataHandler;
import com.example.cyberaware.Fragments.FragmentAssessment;
import com.example.cyberaware.Fragments.FragmentFeedback;
import com.example.cyberaware.Fragments.FragmentProfile;
import com.example.cyberaware.Fragments.NavigationDrawerFragment;
import com.example.cyberaware.R;
/*
    Main activity of the application
    A number of user interface types were implemented for navigation including a tabbed layout(chosen),
    drawer and toolbar. All are kept in the final application for display purposes.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mPager;
    private TabLayout mTabs;
    private ViewPagerAdapter mAdapter;
    private Toolbar toolbar;

    @Override
    @TargetApi(22)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
            getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        setupTabs();
        setupManagers();
    }

    //Setups the managers
    private void setupManagers(){
        DataHandler.init(this);
    }
    //Setups the tabs for the user interface.
    private void setupTabs(){
        mPager = findViewById(R.id.pager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mTabs = findViewById(R.id.tabs);
        mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabs.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
        mTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        mTabs.setTabRippleColorResource(R.color.colorPrimaryLight);

        mTabs.setupWithViewPager(mPager);

        for(int i=0; i < mTabs.getTabCount(); i++){
            TabLayout.Tab tab = mTabs.getTabAt(i);
            tab.setIcon(mAdapter.getIcon(i));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataHandler.unregisterReceivers();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter{


        int icons[] = {R.drawable.ic_home, R.drawable.ic_assessment, R.drawable.ic_feedback};

        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager fm){
            super(fm);
            fragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position){
                case 0:
                    fragment = FragmentProfile.newInstance("","");
                    break;
                case 1:
                    fragment = FragmentAssessment.newInstance("","");
                    break;
                case 2:
                    fragment = FragmentFeedback.newInstance("","");
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabs)[position];
        }

        private Drawable getIcon(int position){
            return ContextCompat.getDrawable(getApplicationContext(),icons[position]);
        }

        @Override
        public int getCount() {
            return 3;
        }


    }
}

