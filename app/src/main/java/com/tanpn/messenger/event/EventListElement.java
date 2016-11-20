package com.tanpn.messenger.event;

import com.tanpn.messenger.utils.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phamt_000 on 11/2/16.
 */
public class EventListElement {
    public String id;
    public String title;
    public String creater;
    public Calendar datetime;
    public Event.EventType type;
    public Map<String, String> pictures;
    public Event.Reminder remind;
    public boolean notify;
    public int days = 0;

    public EventListElement(String _id, String _title, Event.EventType _type, String _date, String _time, String _creater, Event.Reminder _remind, boolean _notify, Map<String, String> _pictures){
        id = _id;
        title = _title;

        datetime = utils.stringToCalendar(_date, _time);

        creater = _creater;
        type = _type;

        pictures = new HashMap<>(_pictures);

        remind = _remind;
        notify = _notify;


        days = utils.getDiffDays(datetime);

    }
}
