package com.tanpn.messenger.fragment_dialog;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDateDialog extends DialogFragment {


    public EventDateDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_date_dialog, container, false);
    }

}
