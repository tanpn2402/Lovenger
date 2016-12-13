package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.EventListAdapter;
import com.tanpn.messenger.event.EventListElement;
import com.tanpn.messenger.event.EventView;
import com.tanpn.messenger.event.EventDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tanpn.messenger.setting.GroupManager;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEvent extends Fragment implements EventListAdapter.OnEventListener, AdapterView.OnItemClickListener,
        GroupManager.onGroupChange, ChildEventListener {


    private FloatingActionButton fabAdd;
    private ListView lvEventList;
    private TextView tvEventDays;
    private EventListAdapter eventListAdapter;
    private DatabaseReference eventRef;


    private PrefUtil prefUtil;

    public FragmentEvent() {
        // Required empty public constructor
    }

    private FirebaseDatabase root;
    private void initFirebase(){
        root = FirebaseDatabase.getInstance();
        eventRef  = root.getReference(prefUtil.getString(R.string.pref_key_current_groups)).child("event");
        // root / <group id> / event / ....

        eventRef.addChildEventListener(this);

        /*{
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                List<String> im = new ArrayList<>();
                im.add("d");

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
                if(event != null){
                    eventListAdapter.edit(event);
                    eventListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // dataSnapshot.getKey() = event ID
                eventListAdapter.delete(dataSnapshot.getKey());
                eventListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });*/
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        prefUtil = new PrefUtil(getContext());

        fabAdd = (FloatingActionButton) v.findViewById(R.id.fab_add);
        fabAdd.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#F44336")));


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewEvent = new Intent(getContext(), EventDetail.class);
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

        //EventListElement event1 = new EventListElement("183124", "tai", EventType.BIRTHDAY, "Nov 03, 2017", "17:03", "tan",Reminder.FIFTEEN_MINUTE, true, im);
        //eventListAdapter.add(event1);

        initFirebase();

        lvEventList.setAdapter(eventListAdapter);
        lvEventList.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                    //Log.d("SCROLL", "SCROLL DOWN");
                                    fabAdd.show();
                                    break;

                                } else if (yDiff < 0.0) {
                                    //Log.d("SCROLL", "SCROLL up");
                                    fabAdd.hide();
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

        lvEventList.setOnItemClickListener(this);

        // set background
        setBackGround(v);

        return v;


    }

    public static void setBackGround(View view){
        ((RelativeLayout)view.findViewById(R.id.layout)).setBackgroundResource(R.drawable.image_background);
    }

    @Override
    public void onDirtyStateChanged(boolean dirty) {}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EventListElement element = (EventListElement) adapterView.getItemAtPosition(i);

        Intent in = new Intent(getContext(), EventView.class);
        JSONObject object = utils.eventToJSONObject(element);
        if(object != null){
            in.putExtra("data", object.toString());
        }
        else
            in.putExtra("data", "null");


        startActivity(in);

    }


    /**
     * khi thay doi group
     *
     * */
    @Override
    public void onChange(String data) {
        eventRef.removeEventListener(this);
        eventListAdapter.deleteAll();
        eventListAdapter.notifyDataSetChanged();


        eventRef  = root.getReference(data).child("event");
        eventRef.addChildEventListener(this);
    }


    /**
     * firebase
     * */
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        List<String> im = new ArrayList<>();
        im.add("d");

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
        if(event != null){
            eventListAdapter.edit(event);
            eventListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        // dataSnapshot.getKey() = event ID
        eventListAdapter.delete(dataSnapshot.getKey());
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
