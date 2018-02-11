package com.example.runningh.plugintestdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by RunningH on 2018/2/6.
 */

public class IActivityManagerHandler implements InvocationHandler {
    private Object iActivityManagerHandler;

    public IActivityManagerHandler(Object iActivityManagerHandler) {
        this.iActivityManagerHandler = iActivityManagerHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            Log.d("ABC", "startActivity 被拦截了");

            Intent rawIntent = null;
            int intentIndex = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    rawIntent = (Intent) args[i];
                    intentIndex = i;
                    break;
                }
            }
            String packageName = MainApplication.getContext().getPackageName();
            Intent newIntent = new Intent();
            if (rawIntent != null) {
                ComponentName componentName = new ComponentName(packageName, PitActivity.class.getName());
                newIntent.setComponent(componentName);
                newIntent.putExtra(ActivityHookHelper.RAW_INTENT, rawIntent);
                args[intentIndex] = newIntent;
                Log.d("ABC", "startActivity hook 成功");
            }
            return method.invoke(iActivityManagerHandler, args);

        }
        return method.invoke(iActivityManagerHandler, args);
    }
}
