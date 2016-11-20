package com.tanpn.messenger.fragment_dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.event.EventDetail;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventCategoryDialog extends DialogFragment implements View.OnClickListener {

    public static EventCategoryDialog createInstance(String title){
        EventCategoryDialog frag = new EventCategoryDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public EventCategoryDialog() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_event_category_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                (ImageView) dialogView.findViewById(R.id.imSelected5)

        };

        for(int i =0 ; i < imSelected.length; i++){
            imSelected[i].setVisibility(View.INVISIBLE);
        }

        switch (category){

            case ANNIVERSARY:
                imSelected[0].setVisibility(View.VISIBLE);
                break;
            case BIRTHDAY:
                imSelected[1].setVisibility(View.VISIBLE);
                break;
            case HOLIDAY:
                imSelected[2].setVisibility(View.VISIBLE);
                break;
            case LIFE:
                imSelected[4].setVisibility(View.VISIBLE);
                break;
            case TRIP:
                imSelected[3].setVisibility(View.VISIBLE);
                break;
        }


        builder.setView(dialogView);



        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    private Event.EventType category = Event.EventType.ANNIVERSARY;
    public Event.EventType getCategory(){
        return category;
    }

    public void setCategory(Event.EventType _category){
        category = _category;
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select1:
                category = Event.EventType.ANNIVERSARY;
                EventDetail.tvEventCategory.setText("Kỉ niệm");
                this.dismiss(); break;
            case R.id.select2:
                category = Event.EventType.BIRTHDAY;
                EventDetail.tvEventCategory.setText("Sinh nhật");
                this.dismiss(); break;
            case R.id.select3:
                category = Event.EventType.HOLIDAY;
                EventDetail.tvEventCategory.setText("Ngày nghỉ");
                this.dismiss(); break;
            case R.id.select4:
                category = Event.EventType.TRIP;
                EventDetail.tvEventCategory.setText("Du lịch");
                this.dismiss();break;
            case R.id.select5:
                category = Event.EventType.LIFE;
                EventDetail.tvEventCategory.setText("Đời sống");
                this.dismiss(); break;
            case 6:
                // tao them category

                break;
        }
    }
}
