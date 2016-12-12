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

import com.tanpn.messenger.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends DialogFragment {


    public ChangePassword() {
        // Required empty public constructor
    }

    private EditText edtOldPass, edtNewPass, edtConfirm;
    private Button btnChange, btnCancel;

    private void init(View v){
        edtOldPass = (EditText) v.findViewById(R.id.edtOldPass);
        edtNewPass = (EditText) v.findViewById(R.id.edtNewPass);
        edtConfirm = (EditText) v.findViewById(R.id.edtConfirm);

        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnChange = (Button) v.findViewById(R.id.btnChangePassword);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change();
            }
        });



    }

    private void cancel(){
        this.dismiss();
    }


    private String oldPass;
    public void setEdtOldPass(String d){
        oldPass= d;
    }


    public interface onChangePassword{
        void onChange(String data);
    }
    private void change(){
        if(!edtOldPass.getText().toString().equals(oldPass)){
            edtOldPass.setError("sai mật khẩu");
            return;
        }

        if(!validatePassword(edtNewPass))
            return;

        if(!edtNewPass.getText().toString().equals(edtConfirm.getText().toString())){
            edtConfirm.setError("không trùng khớp");
            return;
        }

        onChangePassword onChangePassword = (ChangePassword.onChangePassword)  getActivity();
        onChangePassword.onChange(edtNewPass.getText().toString());

        cancel();

    }

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    private boolean validatePassword(EditText input){
        String text = input.getText().toString();
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(text);

        if(!matcher.matches()){
            // validate by using pattern
            // sai qui dinh
            input.setError("sai định dạng");
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        init(dialogView);



        return builder.setView(dialogView).create();
    }

}
