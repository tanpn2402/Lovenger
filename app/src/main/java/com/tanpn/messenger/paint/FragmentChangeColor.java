package com.tanpn.messenger.paint;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.tanpn.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChangeColor extends DialogFragment implements SeekBar.OnSeekBarChangeListener {


    public FragmentChangeColor() {
        // Required empty public constructor
    }

    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_change_color, null);
        builder.setView(colorDialogView); // add GUI to dialog
        //builder.setTitle(R.string.title_color_dialog);
        alphaSeekBar = (SeekBar) colorDialogView.findViewById(
                R.id.alphaSeekBar);
        redSeekBar = (SeekBar) colorDialogView.findViewById(
                R.id.redSeekBar);
        greenSeekBar = (SeekBar) colorDialogView.findViewById(
                R.id.greenSeekBar);
        blueSeekBar = (SeekBar) colorDialogView.findViewById(
                R.id.blueSeekBar);
        colorView = colorDialogView.findViewById(R.id.colorView);
        alphaSeekBar.setOnSeekBarChangeListener(this);
        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        final PaintView doodleView = getDoodleFragment().getDoodleView();

        color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setDrawingColor(color);
                    }
                }
        );

        return builder.create();
    }

    private FragmentPaint getDoodleFragment() {
        return (FragmentPaint) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b)
            color = Color.argb(alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(), greenSeekBar.getProgress(),
                    blueSeekBar.getProgress());
        colorView.setBackgroundColor(color);
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
