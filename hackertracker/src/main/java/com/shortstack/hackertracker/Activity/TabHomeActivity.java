package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Fragment.GenericRowFragment;
import com.shortstack.hackertracker.Fragment.HomeFragment;
import com.shortstack.hackertracker.Fragment.SettingsFragment;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int DEFAULT_FRAGMENT_INDEX = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.tabs)
//    TabLayout tabLayout;
//    @Bind(R.id.viewpager)
//    ViewPager viewPager;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavView;

    @Bind(R.id.filter)
    FloatingActionButton fab;


    private int mFragmentIndex = DEFAULT_FRAGMENT_INDEX;

    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        mHandler = new Handler();


        initViewPager();


        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        if (savedInstanceState == null) {
            mFragmentIndex = DEFAULT_FRAGMENT_INDEX;
            loadFragment();
        }
    }

    private void loadFragment() {
        // set toolbar title
        //setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(getFragmentTag()) != null) {
            mDrawerLayout.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getCurrentFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, getFragmentTag());
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        Logger.d("Set new fragment." + getFragmentTag() + " " + getFragmenTitle());

        invalidateOptionsMenu();

        getSupportActionBar().setTitle(getFragmenTitle());


        updateFABVisibility();

        //Closing drawer on item click
        mDrawerLayout.closeDrawers();
    }

    private void updateFABVisibility() {
        fab.setVisibility( mFragmentIndex == 1 ? View.VISIBLE : View.GONE );
    }

    private Fragment getCurrentFragment() {
        switch (mFragmentIndex) {
            default:
            case 0:
                return HomeFragment.newInstance();

            case 1:
                return GenericRowFragment.newInstance();

            case 2:
                return MapsActivity.newInstance();

            case 3:
                return new VendorsActivity();

            case 4:
                return new SettingsFragment();
        }
    }


    @OnClick(R.id.filter)
    public void onFilterClick() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if( fragment instanceof GenericRowFragment )  {
                ((GenericRowFragment)fragment).showFilters();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (mFragmentIndex != DEFAULT_FRAGMENT_INDEX) {
                mFragmentIndex = DEFAULT_FRAGMENT_INDEX;
                loadFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private void initViewPager() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mFragmentIndex = getFragmentIndex(item);
        loadFragment();
        return true;
    }

    private int getFragmentIndex( MenuItem item ) {
        return item.getItemId() - R.id.nav_home;
    }

    private String getFragmentTag() {
        return "home_fragment_" + mFragmentIndex;
    }

    private String getFragmenTitle() {
        return activityTitles[mFragmentIndex];
    }
}
