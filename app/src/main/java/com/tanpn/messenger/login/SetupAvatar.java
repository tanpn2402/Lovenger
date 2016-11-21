package com.tanpn.messenger.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tanpn.messenger.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetupAvatar extends DialogFragment {


    interface OnSetupCompletion{
        void OnCompleteWithBitmap(Bitmap b);
        void OnCompleteWithUri(Uri u);
    }

    public SetupAvatar() {
        // Required empty public constructor
    }

    private TextView tvFullname, tvSkip;
    private ImageButton ibtCamera, ibtGallery;

    private String fullname = "";
    public void setFullname(String name){
        fullname = name;
    }

    private void init(View v){
        tvFullname = (TextView) v.findViewById(R.id.tvFullName);
        tvSkip = (TextView) v.findViewById(R.id.tvSkip);
        tvFullname.setText(fullname);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }


        });

        ibtCamera = (ImageButton) v.findViewById(R.id.ibtCamera);
        ibtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAPhoto();
            }
        });

        ibtGallery = (ImageButton) v.findViewById(R.id.ibtGallery);
        ibtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAPhoto();
            }
        });
    }

    private void cancel() {
        this.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_setup_avatar, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        init(dialogView);

        builder.setView(dialogView);
        return builder.create();

    }

    private final int CAMERA_CODE = 1;
    private final int GALLERY_CODE = 1;


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == CAMERA_CODE){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            OnSetupCompletion complete = (OnSetupCompletion)getActivity();
            complete.OnCompleteWithBitmap(photo);

            //thoat
            this.dismiss();
        }
        else if(requestCode == GALLERY_CODE){
            Uri u = data.getData();
            OnSetupCompletion complete = (OnSetupCompletion)getActivity();
            complete.OnCompleteWithUri(u);

            //thoat
            this.dismiss();
        }

    }
}
