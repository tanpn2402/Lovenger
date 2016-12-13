package com.tanpn.messenger.fragment_dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tanpn.messenger.R;
import com.tanpn.messenger.setting.InviteItem;
import com.tanpn.messenger.utils.utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteDialog extends DialogFragment {


    public InviteDialog() {
        // Required empty public constructor
    }

    private TextView tvUsername, tvDateIn, tvTimeIn;
    private ImageView imUserphoto;
    private Button btnDismiss, btnAccept;

    private InviteItem item;
    public void setInviteItem(InviteItem i){
        item = i;
    }

    private void init(View v){
        tvUsername = (TextView) v.findViewById(R.id.tvUsername);
        tvDateIn = (TextView) v.findViewById(R.id.tvDateInvite);
        tvTimeIn = (TextView) v.findViewById(R.id.tvTimeInvite);

        imUserphoto = (ImageView) v.findViewById(R.id.imUserphoto);

        tvUsername.setText(item.name);
        tvDateIn.setText(utils.calendarToDateString(item.datatime));
        tvTimeIn.setText(utils.calendarToTimeString(item.datatime));

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/" +
                item.userPhotoName);
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(photoRef)
                .centerCrop()
                .into(imUserphoto);


        btnAccept = (Button) v.findViewById(R.id.btnAccept);
        btnDismiss = (Button) v.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept();
            }
        });

    }

    /**
     * nó mời mình vào nhóm của nó --> mình sẽ setup nhóm của nó bên account mình
     *
     * nếu accept:
     *      - chỉnh sửa prefs :  pref_.._groups
     *      - chỉnh sửa trên firebase
     *      - 1 nhóm chứa thông tin gì:
     *              + đường link sự kiện của nhóm
     *              + đường link chat
     *              + đường link photo
     * */

    public interface onAcceptInvite{
        void onAccept(InviteItem data);
    }
    private void accept() {
        onAcceptInvite onAcceptInvite = (InviteDialog.onAcceptInvite) getActivity();
        onAcceptInvite.onAccept(item);

        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_invite_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        init(dialogView);





        return builder.setView(dialogView).create();
    }


}
