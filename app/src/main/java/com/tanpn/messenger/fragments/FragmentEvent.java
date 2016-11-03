package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.core.view.Event;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.EventListAdapter;
import com.tanpn.messenger.event.EventListElement;
import com.tanpn.messenger.ui.ActivityAddEvent;

import java.util.ArrayList;
import java.util.List;

import com.tanpn.messenger.event.Event.EventStatus;
import com.tanpn.messenger.event.Event.EventType;
/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEvent extends Fragment {


    private FloatingActionButton fabAdd;
    private ListView lvEventList;
    private TextView tvEventDays;

    public FragmentEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        fabAdd = (FloatingActionButton) v.findViewById(R.id.fab_add);
        fabAdd.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#F44336")));


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewEvent = new Intent(getContext(), ActivityAddEvent.class);
                startActivity(createNewEvent);
            }
        });


        tvEventDays = (TextView) v.findViewById(R.id.tvEventDays);
        lvEventList = (ListView) v.findViewById(R.id.lvEventList);

        List<EventListElement> eventList = new ArrayList<>();
        eventList.add(new EventListElement("Untitled 1", "Nov, 02 2016", "Tan Pham", "1023", EventType.HOLIDAY, EventStatus.FURTURE));
        eventList.add(new EventListElement("Untitled 2", "Nov, 05 2016", "Tan Pham", "1023", EventType.TRIP, EventStatus.FURTURE));
        eventList.add(new EventListElement("Untitled 3", "Nov, 08 2016", "Tan Pham", "1023", EventType.HOLIDAY, EventStatus.FURTURE));
        eventList.add(new EventListElement("Untitled 4", "Nov, 07 2016", "Tan Pham", "1023", EventType.BIRTHDAY, EventStatus.PASS));
        eventList.add(new EventListElement("Untitled 5", "Nov, 05 2016", "Tan Pham", "1023", EventType.ANNIVERSARY, EventStatus.FURTURE));
        eventList.add(new EventListElement("Untitled 1", "Nov, 02 2016", "Tan Pham", "1023", EventType.HOLIDAY, EventStatus.PASS));
        eventList.add(new EventListElement("Untitled 2", "Nov, 05 2016", "Tan Pham", "1023", EventType.LIFE, EventStatus.FURTURE));
        eventList.add(new EventListElement("Untitled 3", "Nov, 08 2016", "Tan Pham", "1023", EventType.ANNIVERSARY, EventStatus.PASS));

        EventListAdapter adapter = new EventListAdapter(getContext(), eventList);

        lvEventList.setAdapter(adapter);

        // set background
        setBackGround(v);

        return v;


    }

    public static void setBackGround(View view){
        ((RelativeLayout)view.findViewById(R.id.layout)).setBackgroundResource(R.drawable.image_background);
    }

}
