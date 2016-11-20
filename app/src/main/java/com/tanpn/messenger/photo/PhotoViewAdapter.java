package com.tanpn.messenger.photo;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/4/16.
 * Adapter nay được dùng để hiện thị ảnh lên View Pager
 *
 * vi chỉ dùng để hiện thị ảnh nên chỉ cần truyền arraylist photo path, không phải map<String, String>
 */
public class PhotoViewAdapter extends PagerAdapter {

    private Context context;
    private Integer[] resID;

    private List<String> photoPaths;

    public PhotoViewAdapter(Context _context){
        context = _context;
        resID = null;
    }

    public PhotoViewAdapter(Context _context, Integer[] i){
        context = _context;
        resID = i;
    }

    public PhotoViewAdapter(Context _context, List<String> i){
        context = _context;
        photoPaths = new ArrayList<>(i);
    }

    @Override
    public int getCount() {
        return photoPaths.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView photo = new ImageView(context);
        // chinh sua image
        photo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        photo.setScaleType(ScaleType.CENTER_INSIDE);

        // set resource
        //photo.setBackgroundResource(resID[position]);

        File file = new File(photoPaths.get(position));
        if(file.exists()){
            Picasso.with(context)
                    .load(file)
                    .into(photo);
        }
        else{
            Picasso.with(context)
                    .load(photoPaths.get(position))
                    .into(photo);
        }


        // add into view pager
        ((ViewPager) container).addView(photo, 0);
        return photo;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
