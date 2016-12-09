package com.tanpn.messenger.login;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by phamt_000 on 11/23/16.
 */
public class SignInAdapter extends FragmentPagerAdapter {

    public SignInAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SignInFragment.newInstance( position);
            case 1:
                return SignInFragment.newInstance( position);
            default:
                return SignInFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
