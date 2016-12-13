package com.tanpn.messenger.setting;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tanpn.messenger.R;

/**
 * created at 26/11/2016
 */
public class NewRequest extends DialogFragment {


    public NewRequest() {
        // Required empty public constructor
    }

    private Button btnReq, btnCancel;
    private EditText edtUserID;


    private void init(View v){
        btnReq = (Button) v.findViewById(R.id.btnRequest);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        edtUserID = (EditText) v.findViewById(R.id.edtUserID);



        btnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public interface onSendRequest{
        void onRequest(String data);
    }
    private void request() {
        onSendRequest on = (onSendRequest) getActivity();
        on.onRequest(edtUserID.getText().toString());

        dismiss();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_new_request, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        init(dialogView);



        return builder.setView(dialogView).create();
    }



}
