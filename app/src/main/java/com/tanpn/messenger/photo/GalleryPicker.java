package com.tanpn.messenger.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tanpn.messenger.R;

import java.io.File;

public class GalleryPicker extends AppCompatActivity {

    private GridView grdImages;
    private FloatingActionButton fabOK;

    private ImageAdapter imageAdapter;      // duoc custom lai
    private String[] arrPath;               // danh sach duong dan tat ca hinh trong device
    private boolean[] thumbnailsselection;  // danh sach cac photo duoc select
    private int ids[];                      // ds cac id
    private int count;                      // so luong photo trong anh, count = ids.length = arrPath.length

    private int photoSelected = 0;
    private TextView tvPhotoSelected;


    /**
     * Overrides methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        grdImages= (GridView) findViewById(R.id.grdImages);
        fabOK = (FloatingActionButton) findViewById(R.id.fab_ok);
        tvPhotoSelected = (TextView) findViewById(R.id.tvPhotoSelected);

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);
        imagecursor.close();

        grdImages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                absListView.setOnTouchListener(new View.OnTouchListener() {
                    private float mInitialY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mInitialY = event.getY();
                                return false;
                            case MotionEvent.ACTION_MOVE:
                                final float y = event.getY();
                                final float yDiff = y - mInitialY;
                                mInitialY = y;
                                if (yDiff > 0.0) {
                                    // scroll down
                                    fabOK.show();
                                    break;

                                } else if (yDiff < 0.0) {
                                    // scroll up
                                    fabOK.hide();
                                    break;

                                }
                                break;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        // btn select photo
        fabOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }


    /**
     * List adapter
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.cusom_gallery_cell, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        photoSelected--;
                        tvPhotoSelected.setText("" + photoSelected);
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        photoSelected++;
                        tvPhotoSelected.setText("" + photoSelected);
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                        photoSelected--;
                        tvPhotoSelected.setText("" + photoSelected);
                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailsselection[id] = true;
                        photoSelected++;
                        tvPhotoSelected.setText("" + photoSelected);
                    }
                }
            });


            try {
                //setBitmap(holder.imgThumb, ids[position]);

                // set image
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int mWidthScreen = display.getWidth();
                int imageSize = mWidthScreen / 3 - 4;       // 3 cot, spacing = 4
                Log.i("PATHa", arrPath[position]);
                Picasso.with(getBaseContext())
                        .load(new File(arrPath[position]))
                        .resize(80,80)
                        .centerCrop()
                        .into(holder.imgThumb);

                holder.imgThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageSize, imageSize);
                holder.imgThumb.setLayoutParams(layoutParams);

            } catch (Throwable e) {}

            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;

            return convertView;
        }
    }


    /**
     *
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}

