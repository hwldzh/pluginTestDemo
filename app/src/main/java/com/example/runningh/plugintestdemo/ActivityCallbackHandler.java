package com.example.runningh.plugintestdemo;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.lang.reflect.Field;

/**
 * Created by RunningH on 2018/2/6.
 */

public class ActivityCallbackHandler implements Handler.Callback {
    private Handler handler;
    private int launchActivity = -1;

    public ActivityCallbackHandler(Handler handler) {
        this.handler = handler;
        try {
            Class<?> innerClass = Class.forName("android.app.ActivityThread$H");
            Field field = innerClass.getDeclaredField("LAUNCH_ACTIVITY");
            field.setAccessible(true);
            launchActivity = (int) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == launchActivity) {
            handleLaunchActivity(msg);
        }
        handler.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        Object activityClientRecord = msg.obj;
        try {
            Field intent = activityClientRecord.getClass().getDeclaredField("intent");
            intent.setAccessible(true);
            Intent intentValue = (Intent) intent.get(activityClientRecord);
            Intent rawIntent = intentValue.getParcelableExtra(ActivityHookHelper.RAW_INTENT);
            if (rawIntent != null) {
                intentValue.setComponent(rawIntent.getComponent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
