package com.tanpn.messenger.photo;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tanpn.messenger.R;
import com.tanpn.messenger.paint.ActivityPaint;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityViewPhoto extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    private ViewPager photoView;
    private TextView tvPhotoIndex;
    private ImageButton ibtDraw, ibtBack, ibtShare, ibtDelete, ibtDetail;

    private int currentPhoto = 0;
    private List<String> listPhoto;
    private PhotoViewAdapter adapter;

    private void init(){
        ibtBack = (ImageButton) findViewById(R.id.ibtBack);
        ibtDraw = (ImageButton) findViewById(R.id.ibtDraw);
        ibtShare = (ImageButton) findViewById(R.id.ibtShare);
        ibtDelete = (ImageButton) findViewById(R.id.ibtDelete);
        ibtDetail = (ImageButton) findViewById(R.id.ibtDetail);

        ibtBack.setOnClickListener(this);
        ibtDraw.setOnClickListener(this);
        ibtShare.setOnClickListener(this);
        ibtDelete.setOnClickListener(this);
        ibtDetail.setOnClickListener(this);

        photoView = (ViewPager) findViewById(R.id.photoView);
        tvPhotoIndex = (TextView) findViewById(R.id.tvPhotoIndex);
    }



    private StorageReference imageRef;
    private DatabaseReference photoRef;
    private FirebaseDatabase root;
    private PrefUtil prefUtil;

    private void initFirebase(){
        prefUtil = new PrefUtil(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        imageRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/photo/");

        root = FirebaseDatabase.getInstance();
        photoRef = root.getReference(prefUtil.getString(R.string.pref_key_current_groups)).child("photo");
        photoRef.addChildEventListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        init();


        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;

        String photos = bundle.getString("photos");
        listPhoto = new ArrayList<>();
        listPhoto = Arrays.asList(photos.substring(1, photos.length() - 1).split(","));

        //listPhoto.add(photos);
        currentPhoto = bundle.getInt("current");

        adapter = new PhotoViewAdapter(this, listPhoto);
        photoView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        photoView.setCurrentItem(currentPhoto);
        tvPhotoIndex.setText(String.valueOf(currentPhoto + 1) + "/" + listPhoto.size());
        photoView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPhoto = position + 1;
                tvPhotoIndex.setText( String.valueOf(currentPhoto) + "/" + listPhoto.size());
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

                //gotoPaint.putExtra(utils.PHOTO_PATH, photo[currentPhoto] );
                startActivity(gotoPaint);
                break;

            case R.id.ibtBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        adapter.add(dataSnapshot.getValue().toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
