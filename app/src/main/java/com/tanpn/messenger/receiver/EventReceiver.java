package com.tanpn.messenger.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.EventListElement;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

/**
 * Created by phamt_000 on 11/21/16.
 */
public class EventReceiver extends BroadcastReceiver {


    public static final String ACTION_RECEIVE_EVENT = "com.tanpn.messenger.ACTION_RECEIVE_EVENT";

    private PrefUtil pref;
    private int eventCounter = 0;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_RECEIVE_EVENT)){


            FirebaseDatabase root = FirebaseDatabase.getInstance();
            root.getReference("event").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    _onChildAdded(dataSnapshot, context);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    _onChildChanged(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    _onChildRemove(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });


        }
    }
    private String generateTitle(DataSnapshot data){
        EventListElement event = utils.readJSONString(data.getValue().toString());
        if(event != null){
            return event.creater + " đã thêm 1 sự kiện mới";
        }
        return null;
    }

    private void showNotification(Context context, String title, String msg){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_sent_gray)
                        .setContentTitle("You have " + eventCounter + " message")
                        .setContentText("This is a test notification")
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // id
        int mNotificationId = 001;

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, builder.build());
    }

    private void _onChildAdded(DataSnapshot data, Context context){
        showNotification(context, "", "");
    }

    private void _onChildRemove(DataSnapshot data){

    }

    private void _onChildChanged(DataSnapshot data){

    }
}
