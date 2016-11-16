package com.tanpn.messenger.event;

/**
 * Created by phamt_000 on 11/2/16.
 */
public class Event {
    public static String ANNIVERSARY = "anniversary";
    public static String BIRTHDAY = "birthday";
    public static String HOLIDAY = "holiday";
    public static String LIFE = "life";
    public static String TRIP = "trip";

    public enum EventType{
        ANNIVERSARY,
        BIRTHDAY,
        HOLIDAY,
        LIFE,
        TRIP
    }

    public enum EventStatus{
        FURTURE,
        PASS
    }

    public enum Reminder{
        NONE,
        FIFTEEN_MINUTE,
        THIRDTY_MINUTE,
        ONE_HOUR,
        ONE_DAY,
        ONE_WEEK,
        ONE_MONTH
    }

}
