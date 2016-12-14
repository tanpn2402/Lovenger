package com.tanpn.messenger;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import com.tanpn.messenger.fragments.FragmentSetting.OnSettingChanged;
import com.tanpn.messenger.pagetransformer.*;
import com.tanpn.messenger.receiver.EventReceiver;
import com.tanpn.messenger.receiver.MessageReceiver;
import com.tanpn.messenger.receiver.NetworkReceiver;
import com.tanpn.messenger.service.AppService;
import com.tanpn.messenger.utils.PrefUtil;

public class MainActivity extends AppCompatActivity implements OnSettingChanged {

    private final String TAG = "tag";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagesAdapter viewPagesAdapter;

    private PrefUtil prefUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPaper);

        prefUtil = new PrefUtil(this);

        viewPagesAdapter = new ViewPagesAdapter(getSupportFragmentManager());
        viewPagesAdapter.addFragments(new FragmentEvent(), getString(R.string.fragment_event));
        //viewPagesAdapter.addFragments(new FragmentPicture(), getString(R.string.fragment_picture));
        viewPagesAdapter.addFragments(new FragmentMessage(), getString(R.string.fragment_message));
        viewPagesAdapter.addFragments(new FragmentSetting(), getString(R.string.fragment_setting));


        viewPager.setAdapter(viewPagesAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);

        changeTab(1);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        //viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        setPageTransformer();


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

    @Override
    public void onChanged(int resId) {
        if(resId == R.string.pref_key_main_menu_effect){

            setPageTransformer();
        }
    }

    private void setPageTransformer(){
        switch (Integer.parseInt(prefUtil.getString(R.string.pref_key_main_menu_effect, "1")) - 1 ){
            case 1: // Depth Page
                viewPager.setPageTransformer(false, new DepthPageTransformer());
                break;
            case 2: // Draw From Back
                viewPager.setPageTransformer(false, new DrawFromBackTransformer());
                break;
            case 3: // Fade Page
                viewPager.setPageTransformer(false, new FadePageTransformer());
                break;
            case 4: // Flip Horizontal
                viewPager.setPageTransformer(false, new FlipHorizontalTransformer());
                break;
            case 5: // Rotate Down
                viewPager.setPageTransformer(false, new RotateDownTransformer());
                break;
            case 6: // Zoom Out Page
                viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
                break;
            default:
                viewPager.setPageTransformer(false, new DefaultPageTransformer());

        }
    }

    private void initReceiver(){
        NetworkReceiver networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

    }


    private void startService(){
        if(!AppService.isRunning(this)){
            AppService.startAppService(this);
        }
    }

    private void stopService(){
        if(AppService.isRunning(this)){
            AppService.stopAppService(this);
        }
    }

    private MessageReceiver messageReceiver;
    private EventReceiver eventReceiver;
    private void getMessageNotification(){
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageReceiver.ACTION_RECEIVE_MESSAGE);
        registerReceiver(messageReceiver, filter);
    }

    private void getEventNotification(){
        eventReceiver = new EventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventReceiver.ACTION_RECEIVE_EVENT);
        //registerReceiver(eventReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*unregisterReceiver(messageReceiver);
        //unregisterReceiver(eventReceiver);

        if(!prefUtil.getBoolean(R.string.pref_key_power_saver_mode, false)){
            // khi tat ung dungm kiem tra xem co phai o che do power saver k
            // neu k thi khoi tao service de nhan thong bao

            startService();

        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*stopService();
        if(viewPager.getCurrentItem() == 0){
            // dang o page event thi nhan thong bao cua page message
            getMessageNotification();
        }
        else if(viewPager.getCurrentItem() == 1){
            getEventNotification();
        }*/
    }
}
