package com.tanpn.messenger.login;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tanpn.messenger.R;
import com.tanpn.messenger.pagetransformer.IntroPageTransformer;
import com.tanpn.messenger.pagetransformer.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private Button btn;
    private RelativeLayout footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        btn = (Button) findViewById(R.id.button);
        footer = (RelativeLayout) findViewById(R.id.footer);

        crateFooter();
        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new IntroPageTransformer());

    }

    private final int distance = 30;
    List<ImageView> im = new ArrayList<>();
    private void crateFooter() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = display.getWidth();

        int height = footer.getLayoutParams().height;

        //int width = footer.getLayoutParams().width;

        int n = 5;
        int left = n / 2;

        int startPos = 0;
        if(n % 2 == 0){
            startPos = width / 2 - distance / 2 - left * distance;
        }
        else{
            startPos = width / 2 - left * distance;
        }
        for(int i = 1; i <= n; i++){
            ImageView img = new ImageView(this);
            RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                    10, 10);

            rel_btn.leftMargin = startPos - 5;
            rel_btn.topMargin = height / 2 - 5;
            rel_btn.width = 10;
            rel_btn.height = 10;
            img.setLayoutParams(rel_btn);        // set size

            img.setBackgroundColor(Color.RED);
            startPos += distance;

            footer.addView(img);
            im.add(img);
        }

    }
}
