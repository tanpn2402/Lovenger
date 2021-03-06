package com.tanpn.messenger.event;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by phamt_000 on 11/2/16.
 */
public class EventListAdapter extends BaseAdapter {

    private List<EventListElement> eventList;
    private Context context;

    public EventListAdapter(Context _context){
        context = _context;
        eventList = new ArrayList<>();

    }

    private OnEventListener mListener;

    public void setOnEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public interface OnEventListener {
        public void onDirtyStateChanged(boolean dirty);
    }


    private void notifyDirtyStateChanged(boolean dirty) {

        if (mListener != null) {
            mListener.onDirtyStateChanged(dirty);
        }

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
        ImageView eStatus = (ImageView) v.findViewById(R.id.imEStatus);
        TextView eTitle = (TextView) v.findViewById(R.id.tvEventTitle);
        TextView eDate = (TextView) v.findViewById(R.id.tvEventDate);
        TextView eDays = (TextView) v.findViewById(R.id.tvEDays);
        TextView eCreatedBy = (TextView) v.findViewById(R.id.tvEventCreateBy);


        eIcon.setImageResource(utils.getEventCategoryResourceID(eventList.get(i).type));

        eDays.setText("" + Math.abs(eventList.get(i).days));
        if(eventList.get(i).days > 0){
            // su kien da tao trong qua khu va dang tiep dien ra
            eStatus.setImageResource(utils.getEventStatusResourceID(Event.EventStatus.PASS));
            eDays.setTextColor(Color.parseColor("#2980b9"));
        }
        else{
            // su kien da tao trong tuong lai, con lai xxx ngay nua la den
            eStatus.setImageResource(utils.getEventStatusResourceID(Event.EventStatus.FURTURE));
            eDays.setTextColor(Color.parseColor("#c0392b"));
        }

        eTitle.setText(eventList.get(i).title);
        eDate.setText(utils.calendarToDateString(eventList.get(i).datetime));
        eCreatedBy.setText(eventList.get(i).creater);

        return v;
    }

    public void add(EventListElement event){
        Log.i("TAG", "dsdsdsd");
        Log.i("TAH", event.toString());
        if(!contain(event.id)){

            eventList.add(event);

            notifyDataSetChanged();
        }
    }

    private boolean contain(String eId){
        for(int i = 0 ; i < eventList.size(); i++){
            if(eventList.get(i).id.equals(eId)){
                return  true;
            }
        }
        return false;
    }

    public EventListElement getEvent(String eId){
        for(int i = 0 ; i < eventList.size(); i++){
            if(eventList.get(i).id.equals(eId)){
                return eventList.get(i);
            }
        }

        return null;
    }

    public void edit(EventListElement event){

        EventListElement oldE = getEvent(event.id);

        if(oldE != null){
            oldE.title = event.title;
            oldE.datetime = event.datetime;
            oldE.pictures = event.pictures;
            oldE.notify= event.notify;
            oldE.creater = event.creater;
            oldE.remind = event.remind;
            oldE.type  = event.type;

            notifyDirtyStateChanged(true);
        }

    }

    public void delete(String eId){
        EventListElement event = getEvent(eId);

        if(event != null){
            Log.i("TAG", "delete event " + event.title);
            eventList.remove(event);
            notifyDirtyStateChanged(true);
        }
    }

    public void deleteAll(){
        eventList.clear();
        notifyDirtyStateChanged(true);
    }
}
