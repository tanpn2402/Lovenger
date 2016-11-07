package com.tanpn.messenger.photo;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/3/16.
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context context;
    private Integer[] resID;
    private String[] photoPaths;
    public PhotoListAdapter(Context _context){
        context = _context;
    }

    public PhotoListAdapter(Context _context, Integer[] res){
        context = _context;
        resID = res;
    }

    public PhotoListAdapter(Context _context, String[] res){
        context = _context;
        photoPaths = res;
    }

    @Override
    public int getCount() {
        return photoPaths.length;
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
            imgView = new ImageView(context);
            //can chỉnh lại hình cho đẹp


            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            int mWidthScreen = display.getWidth();
            int imageSize = mWidthScreen / 4 - 4;

            imgView.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imgView.setPadding(8, 8, 8, 8);
        }else{
            imgView=(ImageView) view;
        }

        Picasso.with(context).load(photoPaths[i]).into(imgView);

        return imgView;
    }
}
