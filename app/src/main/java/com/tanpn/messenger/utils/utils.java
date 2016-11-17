package com.tanpn.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.event.Event.EventStatus;
import com.tanpn.messenger.event.EventListElement;
import com.tanpn.messenger.message.MessageListElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by phamt_000 on 11/1/16.
 */
public class utils {

    public static final String FIREBASE_STORABLE_LINK = "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/";

    public static final String FIREBASE_APP_LINK = "https://messenger-d08e4.firebaseio.com/";
    public static final String FIREBASE_MESSAGE_LINK = "https://messenger-d08e4.firebaseio.com/message/";
    public static final String FIREBASE_EVENT_LINK = "https://messenger-d08e4.firebaseio.com/event/";

    public static final String PHOTO_PATH = "photo_path";

    public static List<MessageListElement>  messageList = new ArrayList<>();


    // chuyen tu drawable sang bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // chuyen tu bitmap qua byte array
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        Bitmap bmp = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    // chuyen tu byte array qua bitmap
    public static Bitmap byteArrayToBitmap(byte[] bytes){
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bmp;
    }


    public static String generateEventId(){
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DATE);
        calendar.get(Calendar.MONTH);
        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);
        calendar.get(Calendar.MILLISECOND);

        return "e-" + calendar.get(Calendar.DATE) + "" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.HOUR_OF_DAY) + "" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND);

    }

    public static String generatePhotoName(){
        Calendar calendar = Calendar.getInstance();
        return "c-" + calendar.get(Calendar.DATE) + "" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.HOUR_OF_DAY) + "" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND) + "" + calendar.get(Calendar.MILLISECOND);

    }


    public static int getEventCategoryID(Event.EventType status){
        switch (status){

            case ANNIVERSARY:
                return 1;
            case BIRTHDAY:
                return 2;
            case HOLIDAY:
                return 3;
            case LIFE:
                return 4;
            case TRIP:
                return 5;
        }
        return 1;
    }

    public static Event.EventType getEventCategoryName(int id){
        switch (id){

            case 0:
                return Event.EventType.ANNIVERSARY;
            case 1:
                return Event.EventType.BIRTHDAY;
            case 2:
                return Event.EventType.HOLIDAY;
            case 4:
                return Event.EventType.LIFE;
            case 3:
                return Event.EventType.TRIP;
        }
        return Event.EventType.ANNIVERSARY;
    }
    public static int getEventCategoryResourceID(Event.EventType type){
        switch (type){

            case ANNIVERSARY:
                return R.drawable.ic_anniversary_gray;
            case BIRTHDAY:
                return R.drawable.ic_birthday_gray;
            case HOLIDAY:
                return R.drawable.ic_holiday_gray;
            case LIFE:
                return R.drawable.ic_life_gray;
            case TRIP:
                return R.drawable.ic_trip_gray;
        }
        return R.drawable.ic_anniversary_gray;
    }

    public static int getEventStatusResourceID(EventStatus status){
        switch (status) {
            case FURTURE:
                return R.drawable.ic_day_coming;
            case PASS:
                return R.drawable.ic_day_pass;
        }

        return R.drawable.ic_up_pink;
    }

    public static  String getMonth(int mMonth){
        switch (mMonth){
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";

        }
        return "";
    }

    private static int getMonth(String mMonth){
        switch (mMonth){
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 8;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;

        }
        return 1;
    }

    public static int getDiffDays(Calendar datetime){
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date dt1 = sdf.parse(String.valueOf((today.get(Calendar.MONTH) + 1)) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
            Date dt2 = sdf.parse(String.valueOf((datetime.get(Calendar.MONTH) + 1)) + "/" + datetime.get(Calendar.DAY_OF_MONTH) + "/" + datetime.get(Calendar.YEAR));
            long diff = dt1.getTime() - dt2.getTime();
            return ((int) (diff / (24 * 60 * 60 * 1000)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static Calendar stringToCalendar(String date, String time){

        Calendar calendar = Calendar.getInstance();

        // date format MMM dd, yyyy
        // ex: Nov 02, 2016
        String s = date.replaceAll(",", ""); // delete "," --> Nov 02 2016
        String a[] = s.split(" ");       // -> [0] = Nov, [1] = 02, [2] = 2016

        String t[] = time.split(":");

        calendar.set(Calendar.MONTH, getMonth(a[0]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(a[1]));        // date of month format
        calendar.set(Calendar.YEAR, Integer.parseInt(a[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));         // 24h format
        calendar.set(Calendar.MINUTE, Integer.parseInt(t[1]));

        return calendar;
    }

    public static String calendarToDateString(Calendar c){
        return getMonth(c.get(Calendar.MONTH) +1) + " " + c.get(Calendar.DAY_OF_MONTH) + ", " + c.get(Calendar.YEAR);
    }

    public static String calendarToTimeString(Calendar c){
        return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    }


    public static Event.Reminder getReminder(int i){
        switch (i){
            case 0:
                return Event.Reminder.NONE;
            case 1:
                return Event.Reminder.FIFTEEN_MINUTE;
            case 2:
                return Event.Reminder.THIRDTY_MINUTE;
            case 3:
                return Event.Reminder.ONE_HOUR;
            case 4:
                return Event.Reminder.ONE_DAY;
            case 5:
                return Event.Reminder.ONE_WEEK;
            case 6:
                return Event.Reminder.ONE_MONTH;

        }
        return Event.Reminder.FIFTEEN_MINUTE;
    }

    public static EventListElement readJSONString(String s){

        try {

            JSONObject json = new JSONObject(s);

            List<String> pictures = new ArrayList<>();
            JSONArray list = json.getJSONArray("picture");
            for(int i = 0; i < list.length(); i++){
                pictures.add(list.getString(i));
            }

            EventListElement event = new EventListElement(
                    json.getString("id"),
                    json.getString("title"),
                    utils.getEventCategoryName(json.getInt("category")),
                    json.getString("date"),
                    json.getString("time"),
                    json.getString("creater"),
                    utils.getReminder(json.getInt("remind")),
                    json.getBoolean("notification"),
                    pictures
            );

           return event;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
