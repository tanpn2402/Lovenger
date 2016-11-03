package com.tanpn.messenger.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanpn.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/2/16.
 */
public class EventListAdapter extends BaseAdapter {

    private List<EventListElement> eventList;
    private Context context;
    public EventListAdapter(Context _context, List<EventListElement> list){
        eventList = new ArrayList<>(list);
        context = _context;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =
                inflater.inflate(R.layout.layout_event_element, viewGroup, false);

        ImageView eIcon = (ImageView) v.findViewById(R.id.imEventIcon);
        ImageView eStatus = (ImageView) v.findViewById(R.id.imEventStatus);
        TextView eTitle = (TextView) v.findViewById(R.id.tvEventTitle);
        TextView eDate = (TextView) v.findViewById(R.id.tvEventDate);
        TextView eDays = (TextView) v.findViewById(R.id.tvEventDays);
        TextView eCreatedBy = (TextView) v.findViewById(R.id.tvEventCreateBy);

        setEventIcon(eIcon, eventList.get(i).type);
        setEventStatusImage(eStatus, eventList.get(i).status);

        eTitle.setText(eventList.get(i).title);
        eDate.setText(eventList.get(i).date);
        eDays.setText(eventList.get(i).days);
        eCreatedBy.setText(eventList.get(i).creater);

        return v;
    }

    private void setEventIcon(ImageView view, Event.EventType type){
        switch (type){

            case ANNIVERSARY:
                view.setImageResource(R.drawable.ic_anniversary_gray);
                break;
            case BIRTHDAY:
                view.setImageResource(R.drawable.ic_birthday_gray);
                break;
            case HOLIDAY:
                view.setImageResource(R.drawable.ic_holiday_gray);
                break;
            case LIFE:
                view.setImageResource(R.drawable.ic_life_gray);
                break;
            case TRIP:
                view.setImageResource(R.drawable.ic_trip_gray);
                break;
        }
    }

    private void setEventStatusImage(ImageView view, Event.EventStatus status){
        switch (status){

            case FURTURE:
                view.setImageResource(R.drawable.ic_up_pink);
                break;
            case PASS:
                view.setImageResource(R.drawable.ic_up_white);
                break;
        }
    }
}
