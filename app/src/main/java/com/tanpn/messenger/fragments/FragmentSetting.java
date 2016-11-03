package com.tanpn.messenger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends PreferenceFragment {


    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container,
                savedInstanceState);
    }

    @Override
    public boolean onPreferenceTreeClick() {
        return false;
    }
}
