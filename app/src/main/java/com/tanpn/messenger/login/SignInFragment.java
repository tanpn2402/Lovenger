package com.tanpn.messenger.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;

/**
 * Created by phamt_000 on 11/23/16.
 */
public class SignInFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mPage;

    public static SignInFragment newInstance(int page) {
        SignInFragment frag = new SignInFragment();
        Bundle b = new Bundle();
        b.putInt(PAGE, page);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.layout_signin;
                break;
            case 1:
                layoutResId = R.layout.layout_signup;
                break;
            default:
                layoutResId = R.layout.layout_forgot;
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (mPage){
            case 0:
                signinView(view);
                break;
            case 1:
                signupView(view);
                break;
            default:
                forgotView(view);
        }

    }

    public interface onSignInCompletion{
        void onSignIn(EditText edt1, EditText edt2);                    // duoc kich hoat khi user chon dang nhap
        void onSignUp(EditText edt1, EditText edt2, EditText edt3);     // duoc kich hoat khi user chon dang ki
        void onForgot(EditText edt1);                                   // duoc kich hoat khi uer chon lay lai mat khau
        void onForgetTextviewClick();
    }

    private void signinView(View v){
        final EditText edtUsername, edtPassword;
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtOldPass);

        PrefUtil pre = new PrefUtil(getContext());
        if(!pre.getString(R.string.pref_key_email, "null").equals("null")){
            edtUsername.setText(pre.getString(R.string.pref_key_email));
        }

        Button btnSignin = (Button) v.findViewById(R.id.btnChangePassword);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInCompletion onSignInCompletion = (SignInFragment.onSignInCompletion) getActivity();
                onSignInCompletion.onSignIn(edtUsername,edtPassword);
            }
        });

        TextView tvForgot = (TextView) v.findViewById(R.id.tvForgetPassword);
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInCompletion onSignInCompletion = (SignInFragment.onSignInCompletion) getActivity();
                onSignInCompletion.onForgetTextviewClick();
            }
        });
    }

    private void signupView(View v){
        final EditText edtUsername, edtPassword, edtFullname;
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtOldPass);
        edtFullname = (EditText) v.findViewById(R.id.edtFullname);

        Button btnSignin = (Button) v.findViewById(R.id.btnChangePassword);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInCompletion onSignInCompletion = (SignInFragment.onSignInCompletion) getActivity();
                onSignInCompletion.onSignUp( edtFullname,edtUsername,edtPassword);
            }
        });
    }

    private void forgotView(View v){
        final EditText edtUsername;
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);

        Button btnSignin = (Button) v.findViewById(R.id.btnChangePassword);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInCompletion onSignInCompletion = (SignInFragment.onSignInCompletion) getActivity();
                onSignInCompletion.onForgot(edtUsername);
            }
        });
    }

}
