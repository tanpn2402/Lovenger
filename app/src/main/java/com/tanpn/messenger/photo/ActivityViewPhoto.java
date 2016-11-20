package com.tanpn.messenger.photo;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanpn.messenger.R;
import com.tanpn.messenger.paint.ActivityPaint;
import com.tanpn.messenger.utils.utils;

public class ActivityViewPhoto extends AppCompatActivity implements View.OnClickListener {

    private ViewPager photoView;
    private TextView tvPhotoIndex;
    private ImageView ibtDraw;

    private int currentPhoto = 0;

    private final String[] photo = new String[]{
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/tcu7xozdgqxoxmm5ieyh-1473826982014.jpg?alt=media&token=c4f072a2-2fec-4c68-9673-0c50202c4e83",
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/48.png?alt=media&token=389b9a15-20d4-4aed-a756-4bb42cf370f4",
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/photo%2FC360_2016-02-08-10-45-46-090.jpg?alt=media&token=8c1295e6-20c4-49b1-8b82-497bfbcc07c6"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photoView = (ViewPager) findViewById(R.id.photoView);
        tvPhotoIndex = (TextView) findViewById(R.id.tvPhotoIndex);
        ibtDraw = (ImageView) findViewById(R.id.ibtDraw);
        ibtDraw.setOnClickListener(this);


        //PhotoViewAdapter adapter = new PhotoViewAdapter(this, photo);
        //photoView.setAdapter(adapter);
        //photoView.setCurrentItem(currentPhoto);

        photoView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPhotoIndex.setText(String.valueOf(position+1) + "/" + String.valueOf(photo.length));
                currentPhoto = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtDraw:
                Intent gotoPaint = new Intent(this, ActivityPaint.class);

                gotoPaint.putExtra(utils.PHOTO_PATH, photo[currentPhoto] );
                startActivity(gotoPaint);
                break;
        }
    }
}
