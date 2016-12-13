package com.tanpn.messenger.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;

public class AddFriendActivity extends AppCompatActivity {

    private Button btnJoin;
    private EditText edtUserID;

    private TextView tvHello;

    private PrefUtil pre;
    private void init(){
        pre = new PrefUtil(this);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        edtUserID = (EditText) findViewById(R.id.edtUserID);
        edtUserID.setText(pre.getString(R.string.pref_key_uid));

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGroup();
            }
        });

        tvHello = (TextView) findViewById(R.id.tvHello);
        if(pre.getString(R.string.pref_key_username) != null)
            tvHello.setText("Chào " + pre.getString(R.string.pref_key_username) + "!");
        else
            tvHello.setText("Xin chào!");



        String p = pre.getString(R.string.pref_key_user_photo_link);
        Log.i("photo", p);
        Log.i("photo", getString(R.string.default_user_photo_link));

    }

    private void joinGroup() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        init();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
