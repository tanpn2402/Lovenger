package com.tanpn.messenger.event;

/**
 * Created by phamt_000 on 11/2/16.
 */
public class EventListElement {

    public String title;
    public String date;
    public String creater;
    public String days;
    public Event.EventType type;
    public Event.EventStatus status;

    public EventListElement(String _title, String _date, String _creater, String _days, Event.EventType _type, Event.EventStatus _status){
        title = _title;
        date = _date;
        creater = _creater;
        days = _days;
        type = _type;
        status = _status;
    }
}
