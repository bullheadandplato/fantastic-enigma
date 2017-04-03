package com.osama.smsbomber;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class SendMessageService extends IntentService {

    public SendMessageService(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
