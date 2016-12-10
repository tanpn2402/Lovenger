package com.tanpn.messenger.login;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by phamt_000 on 11/22/16.
 */
public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(Color.parseColor("#cfaf1f"), position);
            case 1:
                return IntroFragment.newInstance(Color.parseColor("#f44973"), position);
            case 2:
                return IntroFragment.newInstance(Color.parseColor("#1fcfbb"), position);
            case 3:
                return IntroFragment.newInstance(Color.parseColor("#3392ff"), position);

            default:
                return IntroFragment.newInstance(Color.parseColor("#c870f4"), position);
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

}
