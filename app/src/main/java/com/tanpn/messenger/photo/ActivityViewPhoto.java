package com.tanpn.messenger.photo;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tanpn.messenger.R;

public class ActivityViewPhoto extends AppCompatActivity {

    private ViewPager photoView;
    private TextView tvPhotoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photoView = (ViewPager) findViewById(R.id.photoView);
        tvPhotoIndex = (TextView) findViewById(R.id.tvPhotoIndex);

        final Integer[] photos = new Integer[]{
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1


        };

        PhotoViewAdapter adapter = new PhotoViewAdapter(this, photos);
        photoView.setAdapter(adapter);
        photoView.setCurrentItem(1);

        photoView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPhotoIndex.setText(String.valueOf(position+1) + "/" + String.valueOf(photos.length));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
