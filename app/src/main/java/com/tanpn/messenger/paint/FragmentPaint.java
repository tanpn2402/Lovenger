package com.tanpn.messenger.paint;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaint extends Fragment {


    public FragmentPaint() {
        // Required empty public constructor
    }

    private PaintView doodleView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;
    // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 100000;

    // used to identify the request for using external storage, which
    // the save image feature needs
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paint, container, false);


        setHasOptionsMenu(true); // this fragment has menu items to display
        // get reference to the DoodleView

        doodleView = (PaintView) view.findViewById(R.id.doodleView);
        //Picasso.with(getContext()).load(ActivityPaint.PHOTO_LINK).into((Target) doodleView);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.paint_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.color:
                FragmentChangeColor colorDialog = new FragmentChangeColor();
                colorDialog.show(getFragmentManager(), "color dialog");
                return true;

            case R.id.line_width:
                FragmentChangeLineWidth widthDialog =
                        new FragmentChangeLineWidth();
                widthDialog.show(getFragmentManager(), "line width dialog");
                return true;

            case R.id.delete_drawing:
                confirmErase();
                return true;

            case R.id.save:
                saveImage();
                return true;


        }


        return super.onOptionsItemSelected(item);
    }

    private void confirmErase() {


    }

    private void saveImage() {
        /*if (getContext().checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                // set Alert Dialog's message
                builder.setMessage(R.string.permission_explanation);
                // add an OK button to the dialog
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // request permission
                                requestPermissions(new String[]{
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                            }

                        }
                );
                builder.create().show();
            }
            else {
                // request permission
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }
        }
        else { // if app already has permission to write to external storage
            doodleView.saveImage(); // save the image
        }*/
    }

    public PaintView getDoodleView() {
        return doodleView;
    }
    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }
}
