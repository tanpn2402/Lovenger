package com.tanpn.messenger;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tanpn.messenger.adapter.ViewPagesAdapter;
import com.tanpn.messenger.fragments.FragmentEvent;
import com.tanpn.messenger.fragments.FragmentMessage;
import com.tanpn.messenger.fragments.FragmentPicture;
import com.tanpn.messenger.fragments.FragmentSetting;
import com.tanpn.messenger.pagetransformer.*;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "tag";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagesAdapter viewPagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPaper);


        viewPagesAdapter = new ViewPagesAdapter(getSupportFragmentManager());
        viewPagesAdapter.addFragments(new FragmentEvent(), getString(R.string.fragment_event));
        //viewPagesAdapter.addFragments(new FragmentPicture(), getString(R.string.fragment_picture));
        viewPagesAdapter.addFragments(new FragmentMessage(), getString(R.string.fragment_message));
        viewPagesAdapter.addFragments(new FragmentSetting(), getString(R.string.fragment_setting));


        viewPager.setAdapter(viewPagesAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);

        changeTab(1);

        final View appbar = findViewById(R.id.appBar);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position + 1);
                Log.i(TAG, position+ "");


                if(position == 2){
                    // tab chat --> hide tabbar
                    //appbar.setVisibility(View.GONE);
                }
                else{
                    //appbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //
        generateMessage();

        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());



    }

    public void changeTab(int i){
        switch (i){
            case 1:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_event_pink);
                //tabLayout.getTabAt(1).setIcon(R.drawable.ic_picture_gray);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_message_gray);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_setting_gray);
                break;
            case 4:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_event_gray);
                //tabLayout.getTabAt(1).setIcon(R.drawable.ic_picture_pink);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_message_gray);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_setting_gray);
                break;
            case 2:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_event_gray);
                //tabLayout.getTabAt(1).setIcon(R.drawable.ic_picture_gray);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_message_pink);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_setting_gray);
                break;
            case 3:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_event_gray);
                //tabLayout.getTabAt(1).setIcon(R.drawable.ic_picture_gray);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_message_gray);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_setting_pink);
                break;

        }
    }

    public void generateMessage(){

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
