package com.tanpn.messenger.paint;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChangeLineWidth extends DialogFragment implements SeekBar.OnSeekBarChangeListener {


    public FragmentChangeLineWidth() {
        // Required empty public constructor
    }

    private ImageView widthImageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View lineWidthDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_change_line_width, null);
        builder.setView(lineWidthDialogView); // add GUI to dialog

        // set the AlertDialog's message
        //builder.setTitle(R.string.title_line_width_dialog);

        // get the ImageView
        widthImageView = (ImageView) lineWidthDialogView.findViewById(
                R.id.widthImageView);

        // configure widthSeekBar
        final PaintView doodleView = getDoodleFragment().getDoodleView();
        final SeekBar widthSeekBar = (SeekBar)
                lineWidthDialogView.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(this);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        // add Set Line Width Button
        builder.setPositiveButton(R.string.button_set_line_width,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setLineWidth(widthSeekBar.getProgress());
                    }
                }
        );

        return builder.create(); // return dialog
    }

    // return a reference to the MainActivityFragment
    private FragmentPaint getDoodleFragment() {
        return (FragmentPaint) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }


    final Bitmap bitmap = Bitmap.createBitmap(
            400, 100, Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(bitmap); // draws into bitmap

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Paint p = new Paint();
        p.setColor(
                getDoodleFragment().getDoodleView().getDrawingColor());
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(i);

        // erase the bitmap and redraw the line
        //bitmap.eraseColor(getResources().getColor(android.R.color.transparent,getContext().getTheme()));
        bitmap.eraseColor(getResources().getColor(R.color.transparent));
        canvas.drawLine(30, 50, 370, 50, p);

        widthImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        FragmentPaint fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        FragmentPaint fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }
}
