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

/**
 * Created by phamt_000 on 11/4/16.
 */
public class PhotoViewAdapter extends PagerAdapter {

    private Context context;
    private Integer[] resID;

    private String[] photoPaths;

    public PhotoViewAdapter(Context _context){
        context = _context;
        resID = null;
    }

    public PhotoViewAdapter(Context _context, Integer[] i){
        context = _context;
        resID = i;
    }

    public PhotoViewAdapter(Context _context, String[] i){
        context = _context;
        photoPaths = i;
    }

    @Override
    public int getCount() {
        return photoPaths.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView photo = new ImageView(context);
        // chinh sua image
        photo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        photo.setScaleType(ScaleType.CENTER);

        // set resource
        //photo.setBackgroundResource(resID[position]);

        Picasso.with(context).load(photoPaths[position]).into(photo);


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
