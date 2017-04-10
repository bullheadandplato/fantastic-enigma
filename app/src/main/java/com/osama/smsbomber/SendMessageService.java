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
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class SendMessageService extends Service {
    private static final String CANCEL="cancel";
    private static final String TAG=SendMessageService.class.getCanonicalName();

    private static SendMessageService instance;
    private static final int MAX_PROGRESS=100;
    private HashMap<Integer,SmsDeliveryController> allDeliver;

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
    private void showNotification(String phone,int count,String message){
        Log.d(TAG, "showNotification: Id count is: "+notificationIdCount);
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
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_black_24dp));

        mNotificationManager.notify(++notificationIdCount,builder.build());
        mNotificationsIds.put(phone,notificationIdCount);
        mAllNotifications.put(notificationIdCount,builder);
        SmsDeliveryController cr=new SmsDeliveryController(count,builder,notificationIdCount,phone,message);
        cr.sendSMS();
        allDeliver.put(notificationIdCount,cr);
    }

    @Override
    public void onCreate() {
        if(!isCreated){
            isCreated=true;
            super.onCreate();
            mNotificationsIds =new HashMap<>();
            mAllNotifications=new HashMap<>();
            allDeliver=new HashMap<>();
            instance=this;
            Log.d(TAG, "onCreate: Im in create");
            launchBroadcast();
        }

    }
    public void startSendingMessages(String phone,int count,String message){
        if(!notAlreadySending(phone)){
            showNotification(phone,count,message);
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
            Log.d(TAG, "onReceive: Stop sending messages.");
            String phone=intent.getExtras().getString("phone");
            int notificationNumber= mNotificationsIds.get(phone);
            mNotificationsIds.remove(phone);
            NotificationCompat.Builder builder=mAllNotifications.get(notificationNumber);
            builder.setOngoing(false);
            builder.setContentText("Canceled");
            mNotificationManager.notify(notificationNumber,builder.build());
            mNotificationManager.cancel(notificationNumber);
            mAllNotifications.remove(notificationNumber);
            allDeliver.get(notificationNumber).cancel();
            allDeliver.remove(notificationNumber);

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

    public class SmsDeliveryController {
        private int count = 0;
        private NotificationCompat.Builder builder;
        private int notificationId;
        private String phone;
        private String message;
        private volatile boolean isCanceled = false;
        private int totalMessages = 0;
        private Thread sendMessageThread;

        SmsDeliveryController(int count, NotificationCompat.Builder bu, int not, String ph, String mes) {
            this.count = count;
            this.builder = bu;
            this.notificationId = not;
            this.phone = ph;
            this.message = mes;
            totalMessages = count;
            Log.d(TAG, "SmsDeliveryController: count is: " + count);
        }

        //---sends an SMS message to another device--
        private int reverseCount = 0;

        public void sendSMS() {
            sendMessageThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    innerSend();
                }
            });
            sendMessageThread.start();
        }

        private void innerSend() {
            Log.d(TAG, "innerSend: called");
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            Log.d(TAG, "sendSMS: sending message");
            --count;
            PendingIntent sentPI = PendingIntent.getBroadcast(SmsBomber.getCtx(), 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(SmsBomber.getCtx(), 0,
                    new Intent(DELIVERED), 0);

            //---when the SMS has been sent---
            registerReceiver(xv, new IntentFilter(SENT));
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, message, sentPI, deliveredPI);
        }
    private BroadcastReceiver xv=new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case RESULT_OK:
                            if(!isCanceled && count!=0) {
                                innerSend();
                            }
                            publishProgress(++reverseCount);
                            break;
                        default:
                    }
                }
            };
    private void publishProgress(int i) {
        if(isCanceled){
            return;
        }
        Log.d(TAG, "publishProgress: progress is: "+i);
        builder.setProgress(totalMessages,i,false);
        if(totalMessages==i){
            builder.setContentText("Done.");
            builder.mActions.clear();
            mNotificationManager.notify(notificationId,builder.build());
            mNotificationManager.cancel(notificationId);
            mAllNotifications.remove(notificationId);
            mNotificationsIds.remove(phone);
            sendMessageThread.interrupt();
            unregisterReceiver(xv);
            return;
        }
        Log.d(TAG, "publishProgress: Notification id is: "+notificationId);
        mNotificationManager.notify(notificationId,builder.build());
    }

        public void cancel() {
            Log.d(TAG, "cancel: cancel abc");
            isCanceled=true;
            sendMessageThread.interrupt();
            unregisterReceiver(xv);
        }
    }



}
