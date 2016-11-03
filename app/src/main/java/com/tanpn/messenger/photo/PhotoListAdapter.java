package com.tanpn.messenger.photo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/3/16.
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context context;
    private Integer[] resID;

    public PhotoListAdapter(Context _context){
        context = _context;
    }

    public PhotoListAdapter(Context _context, Integer[] res){
        context = _context;
        resID = res;
    }

    @Override
    public int getCount() {
        return resID.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imgView;
        if(view == null){
            imgView=new ImageView(context);
            //can chỉnh lại hình cho đẹp
            imgView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setPadding(8, 8, 8, 8);
        }else{
            imgView=(ImageView) view;
        }
        //lấy đúng vị trí hình ảnh được chọn
        //gán lại ImageResource
        imgView.setImageResource(resID[i]);
        return imgView;
    }
}
