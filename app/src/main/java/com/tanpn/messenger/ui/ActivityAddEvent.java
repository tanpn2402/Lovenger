package com.tanpn.messenger.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tanpn.messenger.R;

public class ActivityAddEvent extends AppCompatActivity implements View.OnClickListener {

    private Button btnBack, btnSave, btnVoice, btnPicture, btnVideo, btnRemind;
    private EditText edtComment;

    private boolean backButtonPressed = false;

    private void init(){
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnVoice = (Button) findViewById(R.id.btnVoice);
        btnPicture = (Button) findViewById(R.id.btnPicture);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnRemind = (Button) findViewById(R.id.btnRemind);
        edtComment = (EditText) findViewById(R.id.edtComment);

        btnBack.hasFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        init();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                backButtonPressed = true;
                onBackPressed();
                break;
            case R.id.btnPicture:

                break;
            case R.id.btnVideo:

                break;
            case R.id.btnVoice:

                break;
            case R.id.btnSave:

                break;
            case R.id.btnRemind:

                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (backButtonPressed) {
            backButtonPressed = false;
            super.onBackPressed();
        }
    }
}
