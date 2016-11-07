package com.tanpn.messenger.fragment_dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventRemindDialog extends DialogFragment {


    public EventRemindDialog() {
        // Required empty public constructor
    }

    public static EventRemindDialog createInstance(String title){
        EventRemindDialog frag = new EventRemindDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //String title = getArguments().getString("title");

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_event_remind_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //if(!title.equals(""))
        //    builder.setTitle(title);

        builder.setView(dialogView);

        return builder.create();
    }
}
