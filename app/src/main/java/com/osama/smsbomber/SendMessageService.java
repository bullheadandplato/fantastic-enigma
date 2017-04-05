package com.osama.smsbomber;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class SendMessageService extends IntentService {

    public SendMessageService() {
        super("Yoo");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        showNotification();
    }
    private void showNotification(){
        NotificationManager manger=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(SmsBomber.getCtx())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("03454315404 ")
                .setContentTitle("Sending message");
        manger.notify(1,builder.build());
    }
}
