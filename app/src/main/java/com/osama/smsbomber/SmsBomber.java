package com.osama.smsbomber;

import android.app.Application;
import android.content.Context;

/**
 * Created by bullhead on 4/5/17.
 *
 */

public class SmsBomber extends Application {
    private static SmsBomber ctx;
    public SmsBomber() throws IllegalArgumentException{
        if(ctx==null){
           ctx=this;
        }else{
            throw new IllegalArgumentException("Cannot create toe instance of application.");
        }
    }

    public static SmsBomber getCtx() {
        return ctx;
    }
}
