package com.osama.smsbomber;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class SendMessageService extends Service {
    private static final String CANCEL="cancel";
    private static final String TAG=SendMessageService.class.getCanonicalName();

    private static SendMessageService instance;

    private boolean isCreated=false;
    private boolean isRunning=true;

    private NotificationManager mNotificationManager;
    private HashMap<String,Integer> mNotificationsIds;
    private HashMap<Integer,NotificationCompat.Builder> mAllNotifications;
    private int notificationIdCount=0;

    public SendMessageService() {
        super();

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    private void showNotification(String phone,int count){
        String action=Long.toString(System.currentTimeMillis());
        registerReceiver(rec,new IntentFilter("SMSBomberFilter"));
        Intent cancelIntent=new Intent(this,IntentService.class);
        cancelIntent.setAction(CANCEL);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(
                this,
                new Random().nextInt(),
                new Intent("SMSBomberFilter").putExtra("phone",phone),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(SmsBomber.getCtx())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText(phone)
                .setContentTitle("Sending message")
                .setContentInfo("Sent messages")
                .addAction(R.drawable.ic_cancel_black_24dp,"Cancel",pendingIntent)
                .setOngoing(true);
        mNotificationManager.notify(++notificationIdCount,builder.build());
        mNotificationsIds.put(phone,notificationIdCount);
        mAllNotifications.put(notificationIdCount,builder);
    }
    private void sendMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Log.d(TAG, "run: operation running.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onCreate() {
        if(!isCreated){
            isCreated=true;
            super.onCreate();
            mNotificationsIds =new HashMap<>();
            mAllNotifications=new HashMap<>();
            instance=this;
            Log.d(TAG, "onCreate: Im in create");
            sendMessage();
            launchBroadcast();
        }

    }
    public void startSendingMessages(String phone,int count,String message){
        if(!notAlreadySending(phone)){
            showNotification(phone,count);
        }
    }

    private boolean notAlreadySending(String phone) {
        return mNotificationsIds.containsKey(phone);
    }

    public static SendMessageService getInstance(){
        return instance;
    }

    protected BroadcastReceiver rec=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Stoping service");
            String phone=intent.getExtras().getString("phone");
            int notificationNumber= mNotificationsIds.get(phone);
            mNotificationsIds.remove(phone);
            NotificationCompat.Builder builder=mAllNotifications.get(notificationNumber);
            builder.setOngoing(false);
            builder.setContentText("Canceled");
            mNotificationManager.notify(notificationNumber,builder.build());
            mNotificationManager.cancel(notificationNumber);
            mAllNotifications.remove(notificationNumber);

        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(rec);
        Log.d(TAG, "onDestroy: destroyed");
        isRunning=false;
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: someone just binded me");
        launchBroadcast();
        return new LocalBinder();
    }
    private void launchBroadcast(){
        sendBroadcast(new Intent(CommonConstants.SERVICE_CONTEXT_BROAD));
    }
    public class LocalBinder extends Binder{

    }
}
