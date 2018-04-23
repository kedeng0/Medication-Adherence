package com.example.ben.test_version_2;

import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class HomePage extends AppCompatActivity {
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    public BleManager mBleManager;
    private final static String TAG = HomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        //Ble
        mBleManager = BleManager.getInstance(this);
        if (mBleManager.getDevice() != null) {
            Log.d(TAG, "current device: " + mBleManager.getDevice().getName());

        }

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(mBottomNav);
        mBottomNav.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
        // Set the first appearance of the home tab
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            //mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    // Back to home tab when back button is clicked
    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }
    private void selectFragment(MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.menu_home:
                selectedFragment = ItemHomeFragment.newInstance();
                break;
            case R.id.menu_schedule:
                selectedFragment = ItemScheduleFragment.newInstance();
                break;
            case R.id.menu_status:
                selectedFragment = ItemStatusFragment.newInstance();
                break;
            case R.id.menu_more:
                selectedFragment = ItemMoreFragment.newInstance();
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment,Fragment.class.getSimpleName());
        transaction.commit();

//        if (getSupportFragmentManager().findFragmentByTag(.class.getSimpleName()) == null) {
//            Utils.toast(getApplicationContext(),"add");
//            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//            t.add(R.id.container, selectedFragment, Fragment.class.getSimpleName());
//            t.commit();
//        } else {
//            Utils.toast(getApplicationContext(),"attach");
//            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//            t.attach(getSupportFragmentManager().findFragmentByTag(Fragment.class.getSimpleName()));
//            t.commit();
//        }

        // Uncheck the other items
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }
        updateToolbarText(item.getTitle());
    }

    // Update the title
    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }
}
