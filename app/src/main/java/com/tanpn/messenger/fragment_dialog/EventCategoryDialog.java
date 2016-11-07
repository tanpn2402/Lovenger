package com.tanpn.messenger.fragment_dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventCategoryDialog extends DialogFragment {

    public static EventCategoryDialog createInstance(String title){
        EventCategoryDialog frag = new EventCategoryDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public EventCategoryDialog() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_event_category_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(!title.equals(""))
            builder.setTitle(title);

        builder.setView(dialogView);



        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }


}
