package com.tanpn.messenger.login;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tanpn.messenger.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp extends DialogFragment implements View.OnClickListener {



    public interface onSignUpResult{
        void onResult(String data);
    }

    public SignUp() {
        // Required empty public constructor
    }

    private EditText edtUsername, edtFullname, edtPassword, edtRepeat;
    private Button btnCreate, btnCancel;

    private void init(View v){
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtFullname = (EditText) v.findViewById(R.id.edtFullname);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtRepeat = (EditText) v.findViewById(R.id.edtRepeatPassword);

        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnCreate = (Button)v.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sign_up, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        init(dialogView);

        builder.setView(dialogView);



        return builder.create();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancel:
                cancel();
                break;
            case R.id.btnCreate:
                create(); break;
        }
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private boolean validateEmail(EditText input){
        String text = input.getText().toString();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(text);

        if(!matcher.matches()){
            // validate by using pattern
            // sai qui dinh
            input.setError("sai định dạng");
            return false;
        }
        return true;

    }

    /**
     (			# Start of group
     (?=.*\d)		    #   must contains one digit from 0-9
     (?=.*[a-z])		#   must contains one lowercase characters
     (?=.*[A-Z])		#   must contains one uppercase characters
     .		            #   match anything with previous condition checking
     {6,20}	            #   length at least 6 characters and maximum of 20
     )			# End of group
     * */

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

    public boolean valaditeSamePassword(EditText e1, EditText e2){
        if(e1.getText().toString().equals(e2.getText().toString()))
            return true;

        return false;
    }

    private void create(){
        if(!validateEmail(edtUsername))
            return;

        if(!validatePassword(edtPassword))
            return;

        if(!valaditeSamePassword(edtPassword, edtRepeat))
            return;

        String result = edtFullname.getText().toString() + "|" +
                        edtUsername.getText().toString() + "|" +
                        edtPassword.getText().toString();

        onSignUpResult onSignUpResult = (onSignUpResult) getActivity();
        onSignUpResult.onResult(result);
        cancel();

    }

    private void cancel(){
        this.dismiss();
    }


}
