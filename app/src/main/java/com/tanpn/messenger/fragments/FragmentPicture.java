package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import com.tanpn.messenger.photo.ActivityViewPhoto;
import com.tanpn.messenger.photo.PhotoElement;
import com.tanpn.messenger.photo.PhotoListAdapter;
import com.tanpn.messenger.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class FragmentPicture extends Fragment implements PhotoListAdapter.OnEventListener {


    public FragmentPicture() {
        // Required empty public constructor
    }

    private PhotoListAdapter adapter;

    private GridView photoList;
    private FloatingActionButton fabUpload;

    private StorageReference imageRef;
    private DatabaseReference photoRef;

    private void initFirebase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imageRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/photo/");

        FirebaseDatabase root = FirebaseDatabase.getInstance();
        photoRef = root.getReference("photo");
        photoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try {
                    JSONObject obj = new JSONObject(dataSnapshot.getValue().toString());

                    adapter.add(
                            dataSnapshot.getKey(),
                            new PhotoElement(obj.getLong("size"), obj.getString("url"))
                    );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    /*private final String[] photo = new String[]{
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/tcu7xozdgqxoxmm5ieyh-1473826982014.jpg?alt=media&token=c4f072a2-2fec-4c68-9673-0c50202c4e83",
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/48.png?alt=media&token=389b9a15-20d4-4aed-a756-4bb42cf370f4",
            "gs://messenger-d08e4.appspot.com/photo/C360_2016-02-08-10-45-13-975.jpg"

    };*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        photoList = (GridView) v.findViewById(R.id.photoList);
        fabUpload = (FloatingActionButton) v.findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallery();

            }
        });


        adapter = new PhotoListAdapter(getContext());
        photoList.setAdapter(adapter);


        initFirebase();

        photoList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent viewPhoto = new Intent(getContext(), ActivityViewPhoto.class);
                startActivity(viewPhoto);
            }
        });

        return v;
    }



    private final int GALLERY_CODE = 43;
    private void choosePhotoFromGallery(){
        Intent imagePicker = new Intent(Intent.ACTION_PICK);

        File imgDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String imgPath = imgDir.getPath();

        Uri uri = Uri.parse(imgPath);
        imagePicker.setDataAndType(uri, "image/*");

        startActivityForResult(imagePicker, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_CODE){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final byte[] bytes = baos.toByteArray();


                final String photoName = utils.generateEventId();

                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        UploadTask uploadTask = imageRef.child(photoName).putBytes(bytes);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("size", taskSnapshot.getBytesTransferred());
                                    obj.put("url", taskSnapshot.getDownloadUrl().toString());

                                    photoRef.child(photoName).setValue(obj.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                }.start();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDirtyStateChanged(boolean dirty) {}
}
