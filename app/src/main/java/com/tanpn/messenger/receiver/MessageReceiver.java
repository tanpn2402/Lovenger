package com.tanpn.messenger.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;

/**
 * Created by phamt_000 on 11/21/16.
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String ACTION_RECEIVE_MESSAGE = "com.tanpn.messenger.ACTION_RECEIVE_MESSAGE";

    private PrefUtil pref;
    private int msgCounter = 0;

    /** int result = S1.compareTo(S2)  :
     * result = 0 if S1 = S2 theo từ điển;
     * result > 0 if S1 > S2 theo từ điển;
     * result < 0 if S1 < S2 theo từ điển;

     * */

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("tanna", " child");
        if(intent.getAction().equals(ACTION_RECEIVE_MESSAGE)){
            pref = new PrefUtil(context);
            final String lastMessage = pref.getString(R.string.pref_key_last_message, "null");
            Log.i("tanna", lastMessage);

            FirebaseDatabase root = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = root.getReference("message");
            messageRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.i("tanna", " add child");

                    if (dataSnapshot.getKey().compareTo(lastMessage) > 0) {
                        msgCounter ++;
                        Log.i("tanna", "you have " + msgCounter + " messages");
                        //showNotification(context);

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_notification_white)
                                        .setContentTitle("You have " + msgCounter + " message")
                                        .setContentText("This is a test notification")
                                        .setDefaults(Notification.DEFAULT_ALL)
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
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void showNotification(Context context){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        //.setSmallIcon(R.drawable.abc)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

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
}
