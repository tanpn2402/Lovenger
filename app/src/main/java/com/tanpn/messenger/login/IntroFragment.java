package com.tanpn.messenger.login;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


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
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mBackgroundColor, mPage;

    public static IntroFragment newInstance(int backgroundColor, int page) {
        IntroFragment frag = new IntroFragment();
        Bundle b = new Bundle();
        b.putInt(PAGE, page);
        b.putInt(BACKGROUND_COLOR, backgroundColor);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getArguments().containsKey(BACKGROUND_COLOR))
            throw new RuntimeException("Fragment must contain a \"" + BACKGROUND_COLOR + "\" argument!");
        mBackgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.layout_intro_0;
                break;
            case 1:
                layoutResId = R.layout.layout_intro_1;
                break;
            case 2:
                layoutResId = R.layout.layout_intro_2;
                break;
            case 3:
                layoutResId = R.layout.layout_intro_3;
                break;
            default:
                layoutResId = R.layout.layout_intro_4;
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.intro_background);
        background.setBackgroundColor(mBackgroundColor);

        if(mPage == 4){
            // page so 3 co 3 button
            Button btnSkip =  (Button) view.findViewById(R.id.btnSkip);
            ImageButton ibtCam = (ImageButton) view.findViewById(R.id.ibtCamera);
            ImageButton ibtGal = (ImageButton) view.findViewById(R.id.ibtGallery);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinActivity();
                }
            });

            ibtCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takeAPhoto();
                }
            });

            ibtGal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseAPhoto();
                }
            });
            FirebaseStorage storage = FirebaseStorage.getInstance();
            photoRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/");
        }
    }
    private StorageReference photoRef;  // reference to storage

    private void joinActivity() {
        /**
         * chuyển đến main Activity
         * */
        Intent in = new Intent(getContext(), MainActivity.class);
        startActivity(in);

    }

    private final int CAMERA_CODE = 1;
    private final int GALLERY_CODE = 2;


    private void takeAPhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_CODE);
    }

    private void chooseAPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String picPath = picDir.getPath();

        Uri u = Uri.parse(picPath);
        intent.setDataAndType(u, "image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
    }

    /**
     *
     * interface to communication with SignIn class
     * */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == CAMERA_CODE){
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);   // full quality 100
            byte[] bytes = baos.toByteArray();

            final String photoName = utils.generatePhotoId();
            UploadTask uploadTask = photoRef.child(photoName).putBytes(bytes);  // upload len storage

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {}
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    setupFirebaseAvatar(taskSnapshot.getDownloadUrl().getPath());
                }
            });
        }
        else if(requestCode == GALLERY_CODE){
            Uri u = data.getData();

            final String photoName = utils.generatePhotoId();
            UploadTask uploadTask = photoRef.child(photoName).putFile(u);
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
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(photoPath))
                    .build();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                joinActivity();
                            }
                        }
                    });
        }
    }

}
