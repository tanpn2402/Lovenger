package com.tanpn.messenger.photo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewPhoto extends DialogFragment {


    public PreviewPhoto() {
        // Required empty public constructor
    }

    private String path;
    private String id;      // id của photo, dùng cho việc download
    private PhotoElement photoElement;
    public void setPhotoElement(PhotoElement element){
        /**
         * Chỉnh sửa event: xóa ảnh trong event
         * */
        photoElement = element;
        path = element.url;
    }

    public void setPath(String _path){
        /**
         * Xem ảnh bình thường
         * */
        path = _path;
    }

    private boolean canDownload = false;
    public void setPhotoDetail(Map<String, String> photo){
        for(Map.Entry<String, String> m : photo.entrySet()){
            path = m.getValue();
            id = m.getKey();
        }

        if(path != null && id != null){
            canDownload = true;
            // make sure là đủ thông tin của photo
        }
    }

    private ImageButton ibtDelete;
    private boolean canDelete = false;
    public void setDeletale(boolean can){
        canDelete = can;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_preview_photo, null);

        ImageView imPreview = (ImageView) dialogView.findViewById(R.id.imPreview);

        File file = new File(path);
        if(file.exists()){
            Picasso.with(getActivity())
                    .load(new File(path))
                    //.placeholder(R.drawable.ic_picture_gray)
                    //.error(R.drawable.ic_pic_gray)
                    .into(imPreview);
        }
        else{
            Picasso.with(getActivity())
                    .load(path)
                    //.placeholder(R.drawable.ic_picture_gray)
                    //.error(R.drawable.ic_pic_gray)
                    .into(imPreview);
        }

        ibtDelete = (ImageButton) dialogView.findViewById(R.id.ibtDelete);

        if(canDelete){

            ibtDelete.setVisibility(View.VISIBLE);
            ibtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnResultListener onResultListener = (OnResultListener) getActivity();
                    onResultListener.OnResult(photoElement);
                    hide();
                }
            });
        }
        else if(canDownload){
            ibtDelete.setVisibility(View.VISIBLE);
            ibtDelete.setImageResource(R.drawable.ic_record_gray);
            ibtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        downloadFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        ImageButton ibtBack = (ImageButton) dialogView.findViewById(R.id.ibtBack);
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });



        return builder.create();
    }

    private void hide(){

        this.dismiss();
    }

    private Snackbar downloadNotification;


    private void downloadFile() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(path);

        File localFile = File.createTempFile(id, ".png", new File(utils.getFolderPath("photo")));

        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public interface OnResultListener{
        public void OnResult(PhotoElement photo);
    }
}
