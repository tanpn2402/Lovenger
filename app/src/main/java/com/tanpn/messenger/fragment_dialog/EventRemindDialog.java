package com.tanpn.messenger.fragment_dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.ui.ActivityAddEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventRemindDialog extends DialogFragment implements View.OnClickListener{


    public EventRemindDialog() {
        // Required empty public constructor
    }

    public static EventRemindDialog createInstance(String title){
        EventRemindDialog frag = new EventRemindDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //String title = getArguments().getString("title");

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_event_remind_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);


        //
        RelativeLayout[] select = {
                (RelativeLayout)dialogView.findViewById(R.id.select1),
                (RelativeLayout)dialogView.findViewById(R.id.select2),
                (RelativeLayout)dialogView.findViewById(R.id.select3),
                (RelativeLayout)dialogView.findViewById(R.id.select4),
                (RelativeLayout)dialogView.findViewById(R.id.select5),
                (RelativeLayout)dialogView.findViewById(R.id.select6)
        };

        for(int i = 0; i < select.length; i++){
            select[i].setOnClickListener(this);
        }


        ImageView[] imSelected = {
                (ImageView) dialogView.findViewById(R.id.imSelected1),
                (ImageView) dialogView.findViewById(R.id.imSelected2),
                (ImageView) dialogView.findViewById(R.id.imSelected3),
                (ImageView) dialogView.findViewById(R.id.imSelected4),
                (ImageView) dialogView.findViewById(R.id.imSelected5),
                (ImageView) dialogView.findViewById(R.id.imSelected6)


        };

        for(int i =0 ; i < imSelected.length; i++){
            imSelected[i].setVisibility(View.INVISIBLE);
        }

        switch (reminder) {
            case NONE:imSelected[0].setVisibility(View.VISIBLE);
                break;
            case FIFTEEN_MINUTE:imSelected[1].setVisibility(View.VISIBLE);
                break;
            case THIRDTY_MINUTE:imSelected[2].setVisibility(View.VISIBLE);
                break;
            case ONE_HOUR:imSelected[3].setVisibility(View.VISIBLE);
                break;
            case ONE_DAY:imSelected[4].setVisibility(View.VISIBLE);
                break;
            case ONE_WEEK:imSelected[5].setVisibility(View.VISIBLE);
                break;

        }

        return builder.create();
    }

    private Event.Reminder reminder = Event.Reminder.NONE;
    public Event.Reminder getReminder(){
        return reminder;
    }
    public void setReminder(Event.Reminder _reminder){
        reminder = _reminder;
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select1:
                reminder = Event.Reminder.NONE;
                ActivityAddEvent.tvEventRemind.setText("Không");
                this.dismiss(); break;
            case R.id.select2:
                reminder = Event.Reminder.FIFTEEN_MINUTE;
                ActivityAddEvent.tvEventRemind.setText("Trước 15 phút");
                this.dismiss(); break;
            case R.id.select3:
                reminder = Event.Reminder.THIRDTY_MINUTE;
                ActivityAddEvent.tvEventRemind.setText("Trước 30 phút");
                this.dismiss(); break;
            case R.id.select4:
                reminder = Event.Reminder.ONE_HOUR;
                ActivityAddEvent.tvEventRemind.setText("Trước 1 giờ");
                this.dismiss();break;
            case R.id.select5:
                reminder = Event.Reminder.ONE_DAY;
                ActivityAddEvent.tvEventRemind.setText("Trước 1 ngày");
                this.dismiss(); break;
            case R.id.select6:
                reminder = Event.Reminder.ONE_WEEK;
                ActivityAddEvent.tvEventRemind.setText("Trước 1 tuần");
                this.dismiss(); break;
        }
    }
}
