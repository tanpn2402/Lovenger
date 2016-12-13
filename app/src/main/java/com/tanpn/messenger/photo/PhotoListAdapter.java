package com.tanpn.messenger.photo;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tanpn.messenger.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phamt_000 on 11/3/16.
 * Adapter này được dùng để hiện thji hình ảnh lên grid view
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context context;
    private Map<String, PhotoElement> photoPaths;
    private List<String> keyPath;
    public PhotoListAdapter(Context _context){
        context = _context;
        photoPaths = new HashMap<>();
        keyPath = new ArrayList<>();
    }


    private OnEventListener mListener;

    public void setOnEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public interface OnEventListener {

        public void onDirtyStateChanged(boolean dirty);
    }


    private void notifyDirtyStateChanged(boolean dirty) {

        if (mListener != null) {
            mListener.onDirtyStateChanged(dirty);
        }

    }

    @Override
    public int getCount() {
        return photoPaths.size();
    }

    @Override
    public Object getItem(int i) {
        // return photo element
        // map chỉ get valuse thôi hở

        return photoPaths.get(keyPath.get(i));
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

        long scale = photoPaths.get(keyPath.get(i)).size / 1000000;
        int reSize = 100;
        if(scale >= 2)
            reSize = 80;
        else if(scale >= 4)
            reSize = 60;
        else if(scale >= 6)
            reSize = 40;
        else if(scale >= 8)
            reSize = 20;

        Log.i("SCALE", "scale = " + reSize);

        File file = new File(photoPaths.get(keyPath.get(i)).url);
        if(file.exists()){
            Picasso.with(context)
                    .load(file)
                    .placeholder(R.drawable.ic_picture_gray)
                    .error(R.drawable.ic_picture_gray)
                    .into(imgView);

        }
        else{
            // load photo tu firebase
            Picasso.with(context)
                    .load(photoPaths.get(keyPath.get(i)).url)
                    .resize(reSize, reSize)
                    .centerCrop()
                    .placeholder(R.drawable.ic_picture_gray)
                    .error(R.drawable.ic_picture_gray)
                    .into(imgView);
        }

        return imgView;
    }

    public void add(String key, PhotoElement element){
        if(!photoPaths.containsKey(key)){
            keyPath.add(key);
            photoPaths.put(key, element);

            notifyDataSetChanged();
        }
    }

    public void delete(String key){
        if(photoPaths.containsKey(key)){
            photoPaths.remove(key);
            keyPath.remove(key);

            notifyDirtyStateChanged(true);
        }
    }

    public boolean contain(PhotoElement element){
        if(keyPath.contains(element.id))
            return true;

        return false;
    }

    public void deleteAll(){
        keyPath.clear();
        photoPaths.clear();

        notifyDirtyStateChanged(true);
    }
}
