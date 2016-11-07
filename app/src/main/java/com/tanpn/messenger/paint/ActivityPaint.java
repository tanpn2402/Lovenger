package com.tanpn.messenger.paint;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.utils;

public class ActivityPaint extends AppCompatActivity {

    public static String PHOTO_LINK = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            PHOTO_LINK = bundle.getString(utils.PHOTO_PATH);
        }*/




        // determine screen size
        int screenSize =
                getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK;
        // use landscape for extra large tablets; otherwise, use portrait
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
