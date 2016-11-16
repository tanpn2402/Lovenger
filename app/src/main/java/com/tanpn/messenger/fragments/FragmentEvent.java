package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.core.view.Event;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.EventListAdapter;
import com.tanpn.messenger.event.EventListElement;
import com.tanpn.messenger.ui.ActivityAddEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tanpn.messenger.event.Event.EventStatus;
import com.tanpn.messenger.event.Event.EventType;
import com.tanpn.messenger.event.Event.Reminder;
import com.tanpn.messenger.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEvent extends Fragment implements EventListAdapter.OnEventListener, AdapterView.OnItemClickListener {


    private FloatingActionButton fabAdd;
    private ListView lvEventList;
    private TextView tvEventDays;
    private EventListAdapter eventListAdapter;
    private DatabaseReference eventRef;

    public FragmentEvent() {
        // Required empty public constructor
    }

    private void initFirebase(){
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        eventRef = root.getReference("event");

        eventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                List<String> im = new ArrayList<>();
                im.add("d");
                //eventListAdapter.add(new EventListElement("183123", "tan", EventType.BIRTHDAY, "Nov 02, 2015", "16:03", "tan", true, true, im));
                //eventListAdapter.add(new EventListElement("183128", "tan", EventType.BIRTHDAY, "Nov 02, 2015", "16:03", "tan", true, true, im));


                Log.i("TAG", dataSnapshot.getValue().toString());
                EventListElement event = utils.readJSONString(dataSnapshot.getValue().toString());
                if(event != null)
                {
                    Log.i("TAG", "child added :" );

                    eventListAdapter.add(event);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("TAG", "child changed");

                EventListElement event = utils.readJSONString(dataSnapshot.getValue().toString());
                if(event != null)
                    eventListAdapter.edit(event);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // dataSnapshot.getKey() = event ID
                eventListAdapter.delete(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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

        Calendar c = Calendar.getInstance();
        c.set(2013,2,14);
        tvEventDays.setText("" + utils.getDiffDays(c));


        lvEventList = (ListView) v.findViewById(R.id.lvEventList);

        eventListAdapter = new EventListAdapter(getContext());

        List<String> im = new ArrayList<>();
        im.add("d");

        EventListElement event1 = new EventListElement("183124", "tai", EventType.BIRTHDAY, "Nov 03, 2017", "17:03", "tan",Reminder.FIFTEEN_MINUTE, true, im);

        eventListAdapter.add(event1);

        initFirebase();

        lvEventList.setAdapter(eventListAdapter);

        // set background
        setBackGround(v);

        return v;


    }

    public static void setBackGround(View view){
        ((RelativeLayout)view.findViewById(R.id.layout)).setBackgroundResource(R.drawable.image_background);
    }

    @Override
    public void onLoadComplete() {}

    @Override
    public void onDirtyStateChanged(boolean dirty) {}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
