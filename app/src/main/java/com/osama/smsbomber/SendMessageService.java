package com.osama.smsbomber;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class SendMessageService extends Service {
    private static final String CANCEL="cancel";
    private static final String TAG=SendMessageService.class.getCanonicalName();
    private boolean isRunning=true;

    public SendMessageService() {
        super();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    private void showNotification(){
        registerReceiver(rec,new IntentFilter("SMSBomberFilter"));
        Intent cancelIntent=new Intent(this,IntentService.class);
        cancelIntent.setAction(CANCEL);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,new Intent("SMSBomberFilter"),PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manger=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(SmsBomber.getCtx())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("03454315404 ")
                .setContentTitle("Sending message")
                .addAction(R.drawable.ic_cancel_black_24dp,"Cancel",pendingIntent)
                .setContentIntent(pendingIntent);
        manger.notify(1,builder.build());
    }
    void sendMessage(){
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
        super.onCreate();
        Log.d(TAG, "onCreate: Im in create");
        showNotification();
        sendMessage();
    }

    protected BroadcastReceiver rec=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(rec);
            Log.d(TAG, "onReceive: Stoping service");
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroyed");
        isRunning=false;
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
