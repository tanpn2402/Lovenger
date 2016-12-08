package com.tanpn.messenger.event;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanpn.messenger.R;
import com.tanpn.messenger.pagetransformer.FlipHorizontalTransformer;
import com.tanpn.messenger.photo.PhotoElement;
import com.tanpn.messenger.photo.PhotoViewAdapter;
import com.tanpn.messenger.utils.utils;

import java.util.ArrayList;
import java.util.List;

public class EventView extends AppCompatActivity implements View.OnClickListener {

    private TextView tvEventTitle, tvEventDays, tvEventDate, tvEventTime, tvEventCreated;
    private ViewPager viewPager;

    ImageView ibtEventDetail, ibtEventShare, ibtEventDelete;

    private List<String> photoPaths;
    private EventListElement event;
    private String eventString;

    private void init(){
        tvEventDays = (TextView) findViewById(R.id.tvEventDays);
        tvEventTitle = (TextView) findViewById(R.id.tvEventTitle);

        tvEventDate = (TextView) findViewById(R.id.tvEventDate);
        tvEventTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventCreated = (TextView) findViewById(R.id.tvEventCreated);

        ibtEventDetail = (ImageView) findViewById(R.id.ibtEventDetail);
        ibtEventDetail.setOnClickListener(this);
        ibtEventShare = (ImageView) findViewById(R.id.ibtEventShare);
        ibtEventShare.setOnClickListener(this);
        ibtEventDelete = (ImageView) findViewById(R.id.ibtEventDelete);
        ibtEventDelete.setOnClickListener(this);



        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            eventString = bundle.getString("data");
            event = utils.readJSONString(eventString);
        }

        if(event == null)
            return;

        tvEventCreated.setText(event.creater);
        tvEventTitle.setText(event.title);
        tvEventDays.setText("123");
        tvEventDate.setText(utils.calendarToDateString(event.datetime));
        tvEventTime.setText(utils.calendarToTimeString(event.datetime));


        viewPager = (ViewPager) findViewById(R.id.photoView);
        PhotoViewAdapter adapter = new PhotoViewAdapter(this, new ArrayList<>(event.pictures.values()));
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(currentPhoto);

        viewPager.setPageTransformer(false, new FlipHorizontalTransformer());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtEventDetail:
                viewDetail();
                break;
        }
    }


    private void viewDetail(){
        Intent in = new Intent(this, EventDetail.class);
        in.putExtra("data", eventString);
        startActivity(in);
    }
}
