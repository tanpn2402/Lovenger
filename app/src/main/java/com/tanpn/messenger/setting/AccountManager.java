package com.tanpn.messenger.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tanpn.messenger.R;
import com.tanpn.messenger.login.SignIn;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AccountManager extends AppCompatActivity implements ChangePassword.onChangePassword {

    private Button btnLogout, btnChangePassword,btnChangePhoto;
    private ImageView imPhoto;
    private ImageButton ibtBack;
    private TextView tvUsename;

    private PrefUtil prefUtil;

    private Snackbar snackbar;


    private void init(){
        prefUtil = new PrefUtil(this);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prefUtil.put(R.string.pref_key_email, "null");
                prefUtil.put(R.string.pref_key_password, "null");
                prefUtil.put(R.string.pref_key_username, "null");
                prefUtil.put(R.string.pref_key_uid, "null");

                prefUtil.put(R.string.pref_key_groups, "null");
                prefUtil.put(R.string.pref_key_default_group, "null");
                prefUtil.put(R.string.pref_key_current_groups, "null");
                prefUtil.put(R.string.pref_key_current_event, "null");


                prefUtil.put(R.string.pref_key_user_photo_name, "null");
                prefUtil.put(R.string.pref_key_user_photo_link, "null");

                prefUtil.apply();

                backToSignIn();

            }
        });

        ibtBack = (ImageButton) findViewById(R.id.ibtBack);
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePhoto = (Button) findViewById(R.id.btnChangePhoto);
        imPhoto = (ImageView) findViewById(R.id.imPhoto);
        tvUsename = (TextView) findViewById(R.id.tvUsername);

        tvUsename.setText(getString(R.string.login_with_name) + " " + prefUtil.getString(R.string.pref_key_username));



        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        photoRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/");

        String pName = prefUtil.getString(R.string.pref_key_user_photo_name);
        StorageReference aRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/" + pName);

        //Log.i("photo",  );
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(aRef)
                .into(imPhoto);


        ///
        snackbar = Snackbar.make(
                findViewById(R.id.activity_account),
                "",
                Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Ẩn", new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                snackbar.dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        init();

    }

    private void backToSignIn() {
        Intent in = new Intent(this, SignIn.class);
        startActivity(in);

        finish();
    }

    private void changePassword(){
        ChangePassword changePassword = new ChangePassword();
        changePassword.setEdtOldPass(prefUtil.getString(R.string.pref_key_password));
        changePassword.show(getSupportFragmentManager(), "dialog");
    }

    private void changePhoto(){
        //init firebase



        // choose image
        Intent intent = new Intent(Intent.ACTION_PICK);

        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String picPath = picDir.getPath();

        Uri u = Uri.parse(picPath);
        intent.setDataAndType(u, "image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
    }

    private final int GALLERY_CODE = 1;
    private StorageReference photoRef;  // reference to storage
    private String photoName;
    private Uri photoUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_CODE){

            //show snackbar
            snackbar.setText("Đang đổi ảnh đại diện...").setDuration(Snackbar.LENGTH_INDEFINITE).show();

            //
            photoUri = data.getData();

            photoName = prefUtil.getString(R.string.pref_key_user_photo_name);
            if(photoName.equals(getString(R.string.default_user_photo_name))){
                // neu la photo default thi phai tao ten moi cho avatar moi
                photoName = utils.generatePhotoId();
            }
            UploadTask uploadTask = photoRef.child(photoName).putFile(photoUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // upload to storage success
                    setupFirebaseAvatar(taskSnapshot.getDownloadUrl().getPath());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            });
        }
    }

    private void setupFirebaseAvatar(final String photoPath){
        Uri u = Uri.parse(photoPath);
        Log.i("k hieu", u.toString());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(u)
                .build();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // luu pref
                                prefUtil.put(R.string.pref_key_user_photo_name, photoName);
                                prefUtil.put(R.string.pref_key_user_photo_link, photoPath);

                                prefUtil.apply();

                                // thong bao la da doi photo thanh cong
                                try {
                                    changePhotoSuccess();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    private void changePhotoSuccess() throws FileNotFoundException {
        snackbar.setText("Thành công").setDuration(Snackbar.LENGTH_SHORT).show();

        InputStream in = getContentResolver().openInputStream(photoUri);
        Bitmap b = BitmapFactory.decodeStream(in);
        imPhoto.setImageBitmap(b);

    }

    @Override
    public void onChange(final String data) {
        snackbar.setText("Đang thay đổi mật khẩu...").setDuration(Snackbar.LENGTH_INDEFINITE).show();

        // change password
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            prefUtil.put(R.string.pref_key_password, data);
                            prefUtil.apply();

                            snackbar.setText("Thành công").setDuration(Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
