package com.tanpn.messenger.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tanpn.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/25/16.
 */
public class InviteAdapter extends BaseAdapter {
    private Context context;
    private List<InviteItem> listInvites;
    private List<String> inviteID;
    private LayoutInflater inflater;

    public InviteAdapter(Context _context){
        context = _context;
        listInvites = new ArrayList<>();
        inviteID = new ArrayList<>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return listInvites.size();
    }

    @Override
    public Object getItem(int i) {
        return listInvites.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        ImageView imUserPhoto;
        TextView tvUsername;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cusom_gallery_cell, null);
            holder.imUserPhoto = (ImageView) convertView.findViewById(R.id.imUserphoto);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername) ;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // hien thi
        try{
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/" +
                                listInvites.get(i).userPhotoName);
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(photoRef)
                    .centerCrop()
                    .into(holder.imUserPhoto);

            holder.tvUsername.setText(listInvites.get(i).name);
        }
        catch (Exception e){

        }


        return convertView;
    }

    public void add(InviteItem i){
        if(!inviteID.contains(i.id)){
            inviteID.add(i.id);
            listInvites.add(i);
        }
    }

    public void delete(InviteItem i){
        if(inviteID.contains(i.id)){
           inviteID.remove(i.id);
            listInvites.remove(i);
        }
    }
}
