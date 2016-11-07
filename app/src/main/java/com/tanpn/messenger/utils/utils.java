package com.tanpn.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.event.Event.EventStatus;
import com.tanpn.messenger.message.MessageListElement;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
        /*calendar.get(Calendar.DATE);
        calendar.get(Calendar.MONTH);
        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);*/

        return "e-" + calendar.get(Calendar.DATE) + "" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.HOUR_OF_DAY) + "" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND);

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

}
