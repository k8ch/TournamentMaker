package com.example.willie.dreamteamtake2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Page adapter for tabs on main screen.
 *
 * @author Anatolie Diordita (Code)
 *
 */
public class Home_PagerAdapter extends FragmentStatePagerAdapter {

    // The number of tabs being handled by the adapter
    private int mNumOfTabs;

    // Initiate page adapter
    public Home_PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    // Returns a fragment (tab view)
    @Override
    public Fragment getItem(int position) {

        // Switch for initializing tab fragments
        switch (position) {
            case 0:
                Home_Overview tab1 = new Home_Overview();
                return tab1;
            case 1:
                Home_Schedule tab2 = new Home_Schedule();
                return tab2;
            case 2:
                Home_Rankings tab3 = new Home_Rankings();
                return tab3;
            default:
                return null;
        }
    }

    // Returns the number of tabs
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}