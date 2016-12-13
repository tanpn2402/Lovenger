package com.tanpn.messenger.photo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SharePhoto extends AppCompatActivity implements PhotoListAdapter.OnEventListener,
        ChildEventListener {


    private PhotoListAdapter adapter;

    private GridView photoList;
    private FloatingActionButton fabUpload;

    private StorageReference imageRef;
    private DatabaseReference photoRef;
    private FirebaseDatabase root;

    private PrefUtil prefUtil;

    private void init(){

        prefUtil = new PrefUtil(this);


        photoList = (GridView) findViewById(R.id.photoList);
        fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallery();

            }
        });


        adapter = new PhotoListAdapter(this);
        initFirebase();
        photoList.setAdapter(adapter);


        initFirebase();

        photoList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                absListView.setOnTouchListener(new View.OnTouchListener() {
                    private float mInitialY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mInitialY = event.getY();
                                return false;
                            case MotionEvent.ACTION_MOVE:
                                final float y = event.getY();
                                final float yDiff = y - mInitialY;
                                mInitialY = y;
                                if (yDiff > 0.0) {
                                    // scroll down
                                    fabUpload.show();
                                    break;

                                } else if (yDiff < 0.0) {
                                    // scroll up
                                    fabUpload.hide();
                                    break;

                                }
                                break;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewPhoto();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(changeGroup, new IntentFilter("CHANGE_GROUP"));


    }

    private void viewPhoto(){
        Intent viewPhoto = new Intent(this, ActivityViewPhoto.class);

        startActivity(viewPhoto);
    }

    /**
     * Local Broadcast
     * */
    private BroadcastReceiver changeGroup = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            onChange(message);
        }
    };

    /**
     * change group
     * */

    public void onChange(String data) {
        photoRef.removeEventListener(this);
        adapter.deleteAll();
        adapter.notifyDataSetChanged();

        photoRef = root.getReference(data).child("photo");
        photoRef.addChildEventListener(this);
    }

    private void initFirebase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imageRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/photo/");

        root = FirebaseDatabase.getInstance();
        photoRef = root.getReference(prefUtil.getString(R.string.pref_key_current_groups)).child("photo");
        photoRef.addChildEventListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photo);

        init();

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            JSONObject obj = new JSONObject(dataSnapshot.getValue().toString());

            adapter.add(
                    dataSnapshot.getKey(),
                    new PhotoElement(obj.getString("id"), obj.getLong("size"), obj.getString("path"))
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}

    @Override
    public void onDirtyStateChanged(boolean dirty) {}


    private final int GALLERY_CODE = 1;
    private void choosePhotoFromGallery(){

        Intent intent = new Intent(this,GalleryPicker.class);
        startActivityForResult(intent,GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("SIZE", "size = " + requestCode + resultCode);
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_CODE){
            Log.i("SIZE", "OK");
            List<String> imagesPathList = new ArrayList<>();
            String[] imagesPath = data.getStringExtra("data").split("\\|");
            List<Bitmap> photos = new ArrayList<>();
            Log.i("SIZE", "size = " + imagesPath.length);
            for (int i=0; i<imagesPath.length; i++){
                imagesPathList.add(imagesPath[i]);
                photos.add(BitmapFactory.decodeFile(imagesPath[i]));

            }

            new uploadPhoto().execute(photos);


        }
    }

    class uploadPhoto extends AsyncTask<List<Bitmap>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(List<Bitmap>... lists) {
            List<Bitmap> photos = lists[0];
            SystemClock.sleep(50);


            for(int i = 0 ; i < photos.size(); i++){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photos.get(i).compress(Bitmap.CompressFormat.PNG, 100, baos);   // full quality 100
                byte[] bytes = baos.toByteArray();

                final String photoName = utils.generatePhotoId();
                UploadTask uploadTask = imageRef.child(photoName).putBytes(bytes);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("id", photoName);
                            obj.put("size", taskSnapshot.getBytesTransferred());
                            obj.put("path", taskSnapshot.getDownloadUrl().toString());

                            photoRef.child(photoName).setValue(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                SystemClock.sleep(100);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            /*Snackbar snackbar = Snackbar
                    .make(v.findViewById(R.id.fragment_picture), "Message is deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(v.findViewById(R.id.fragment_picture), "Message is restored!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();*/
        }
    }
}
