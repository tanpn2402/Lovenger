package com.tanpn.messenger.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.receiver.MessageReceiver;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

public class AppService extends Service {


    public AppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    /**
     * duoc dung de stop service
     */
    private static final String ACTION_STOP = "com.tanpn.messenger.action_stop";
    /**
     * duoc dung de start alarm
     */
    private static final String ACTION_START = "com.tanpn.messenger.action_start";
    /**
     * Khi xac dinh action nay, service se khoi tao lai tat ca doi tuong
     * Nô chi co hieu luc khi service duoc start 1 cach tuong minh
     */
    private static final String ACTION_RESTART = "com.tanpn.messenger.action_restart";
    private static final String EXTRA_FORCE_RESTART = "com.tanpn.messenger.force_restart";

    private static final int REQUEST_CODE = 0x1234AF;

    private boolean allowDestroy;
    private boolean allowRestart;

    private BroadcastReceiver networdChecker;

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    internetConnected(true);
                } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                    internetConnected(false);
                }
            }
        }
    }


    private void internetConnected(boolean isConnect){
        if(isConnect){
            Log.i("tag", "connect");
        }
        else{
            Log.i("tag", "disconnect");
        }
    }

    /*public class MessageReceiver extends BroadcastReceiver {

        public static final String ACTION_RECEIVE_MESSAGE = "com.tanpn.messenger.ACTION_RECEIVE_MESSAGE";

        private PrefUtil pref;
        private int msgCounter = 0;

        /** int result = S1.compareTo(S2)  :
         * result = 0 if S1 = S2 theo từ điển;
         * result > 0 if S1 > S2 theo từ điển;
         * result < 0 if S1 < S2 theo từ điển;

         *
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
                                            .setSmallIcon(R.drawable.ic_sent_gray)
                                            .setContentTitle("You have " + msgCounter + " message")
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

    }*/

        @Override
    public void onCreate() {
        super.onCreate();
    }


    private boolean init(){
        networdChecker = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networdChecker, filter);

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.ACTION_RECEIVE_MESSAGE); sendBroadcast(intent);

        return true;
    }


    private boolean explicitStarted;
    // kiem tra xem service duoc start 1 cach tuong minh hay khong
    // start 1 cach tuong minh co nghia la: nếu được gọi bằng lệnh start thì đó là tường minh,
    //      nếu được gọi bằng alarm thì đó là k tường minh

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null || ACTION_START.equals(intent.getAction())){
            // service duoc start
            Log.i("tag", "action start");
            if(!explicitStarted){

                if(!init()){
                    // khong khoi tao duoc cac doi tuong
                    doStopService();
                    // dung service
                    return START_NOT_STICKY;
                }
                explicitStarted = true;
            }

            // thực hiện công việc tại đây


        }
        else if(ACTION_RESTART.equals(intent.getAction())){
            // service duoc restart
            Log.i("tag", "service restart");

            if(intent.getBooleanExtra(EXTRA_FORCE_RESTART, false)){
                doRestartService();
            }
            else{
                doStopService();
            }
        }
        else{
            Log.i("tag", "stop service");
            doStopService();
        }


        return START_STICKY;
    }




    // duoc dung de bat/tat service
    public static boolean toggleService(Context c){
        if(isRunning(c)){
            Log.i("tag:", "toggle - stop");
            stopAppService(c);
            return false;
        }
        else{
            Log.i("tag:", "toggle - start");
            startAppService(c);
            return true;
        }

    }

    // kiem tra xem service co dang chay hay khong
    public static boolean isRunning(Context c){
        ActivityManager man = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        /// man giu cac service dang chay tren device
        for(ActivityManager.RunningServiceInfo service : man.getRunningServices(Integer.MAX_VALUE)){
            // duyet trong man xem coi co service nao co ten == ten service minh tao ra hay khong (AppLockService)
            if(AppService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    public static void stopAppService(Context c){
        stopAlarm(c);

        // tao 1 intent de chay service nay voi action = stop
        Intent i = new Intent(c, AppService.class);
        i.setAction(ACTION_STOP);
        c.startService(i);

    }

    public static void startAppService(Context c){
        startAlarm(c);
    }


    private static PendingIntent running_intent;
    private static PendingIntent getRunIntent(Context c){
        Log.i("tag", "get running intent");

        if(running_intent == null){
            Intent i = new Intent(c, AppService.class);
            i.setAction(ACTION_START);
            running_intent = PendingIntent.getService(c, REQUEST_CODE, i,0);

            /*
             * PendingIntent getActivities (Context context,
                                            int requestCode,
                                            Intent intents,
                                            int flags)
             *  context 	    Context: The Context in which this PendingIntent should start the activity.
                requestCode 	int: Private request code for the sender
                intent      	Intent: Intent of the activity to be launched.
                flags 	        int: May be FLAG_ONE_SHOT, FLAG_NO_CREATE, FLAG_CANCEL_CURRENT, FLAG_UPDATE_CURRENT,
             or any of the flags as supported by Intent.fillIn() to control which unspecified parts of the intent that can be supplied when the actual send happens.
             *
             *
             */

        }

        return running_intent;
    }



    public static void startAlarm(Context c){
        AlarmManager am = (AlarmManager) c.getSystemService(ALARM_SERVICE);
        PendingIntent pi = getRunIntent(c);
        long interval = 250;
        long startTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, startTime, interval, pi);
        Log.i("tag", "alarm started");
        /*
        * void setRepeating (int type,
                long triggerAtMillis,
                long intervalMillis,
                PendingIntent operation)
        * type:              ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC, or RTC_WAKEUP.
        * triggerAtMiilis:   thoi gian dau tien ma alarm thuc hien hanh dong nao do
        * intervalMillis:    khoang cach giua 2 lan lap cua alarm
        * operation:        hanh dong de thuc hien khi alarm off
        * see at: https://developer.android.com/reference/android/app/AlarmManager.html#setRepeating(int,%20long,%20long,%20android.app.PendingIntent)
        * */
    }

    public static void stopAlarm(Context c){
        AlarmManager am = (AlarmManager) c.getSystemService(ALARM_SERVICE);
        am.cancel(getRunIntent(c));
    }

    private void doStopService(){
        stopAlarm(this);
        allowDestroy = true;
        stopForeground(true);
        stopSelf();
    }

    private void doRestartService(){
        allowRestart = true;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
