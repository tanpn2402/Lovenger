package com.tanpn.messenger.event;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 12/9/16.
 */
public class CommentListAdapter extends BaseAdapter {


    class Comment{
        String id;
        String userPhoto;
        String comment;
        String datetime;

        public  Comment(String _id ,String _userphoto, String _comment, String _datetime){
            userPhoto = _userphoto;
            id = _id;
            comment = _comment;
            datetime = _datetime;


        }
    }

    private Context context;
    private List<Comment> listComment;
    private LayoutInflater mInflater;

    public CommentListAdapter(Context context, String jsonString){
        this.context = context;
        listComment = new ArrayList<>();
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            JSONObject obj = new JSONObject(jsonString);

            if(obj.has("comment")){
                JSONArray arr = obj.getJSONArray("comment");
                if(arr != null && arr.length() > 0){
                    for(int i = 0; i< arr.length(); i++){
                        JSONObject o = (JSONObject) arr.get(i);

                        listComment.add(new Comment(o.getString("id"), o.getString("userphoto"), o.getString("content"), o.getString("datetime")));
                        notifyDataSetChanged();
                    }
                }
            }





        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getCount() {
        return listComment.size();
    }

    @Override
    public Object getItem(int i) {
        return listComment.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder{
        ImageView imUserPhoto;
        TextView tvComment;
        TextView tvDatetime;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_comment, null);
            holder.imUserPhoto = (ImageView) convertView.findViewById(R.id.imUserPhoto);
            holder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
            holder.tvDatetime = (TextView) convertView.findViewById(R.id.tvDatetime);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvComment.setText(listComment.get(i).comment);
        holder.tvDatetime.setText(listComment.get(i).datetime);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/" + listComment.get(i).userPhoto);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(photoRef)
                .centerCrop()
                .into(holder.imUserPhoto);

        return convertView;
    }

    public boolean contains(String id){
        for(Comment c : listComment){
            if(c.id.equals(id))
                return true;
        }

        return false;
    }
    public void add(JSONArray arr){

        try {

            JSONObject o = (JSONObject) arr.get(arr.length() - 1);
            Comment comment = new Comment(o.getString("id"), o.getString("userphoto"), o.getString("content"), o.getString("datetime"));
            if(!contains(comment.id)){
                listComment.add(comment);
                notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void add(String jsonString){

        try {
            JSONObject obj = new JSONObject(jsonString);
            if(obj.has("comment")){
                JSONArray arr = obj.getJSONArray("comment");


                for(int i = 0 ; i < arr.length(); i++){
                    JSONObject o = (JSONObject) arr.get(i);
                    Comment comment = new Comment(o.getString("id"), o.getString("userphoto"), o.getString("content"), o.getString("datetime"));

                    if(!contains(comment.id)){
                        listComment.add(comment);
                        notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
