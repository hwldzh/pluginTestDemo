package com.example.runningh.plugintestdemo;

import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by RunningH on 2018/2/5.
 */

public class ActivityHookHelper {
    public static final String RAW_INTENT = "RAW_INTENT";

    /**
     * Hook AMS
     * 主要完成的操作是将真正要启动的Activity替换成在Manifest里面注册的坑位Activity
     */
    public static void hookActivityWithPit() {
        try {
            /*Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefaultValue = gDefaultField.get(null);*/

            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field singletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            singletonField.setAccessible(true);
            Object singletonValue = singletonField.get(null);

            //gDefault是一个 android.util.Singleton对象;取出这个单例里面的字段
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            //gDefault是一个Singleton类型的，我们需要从Singleton中再取出这个单例的AMS代理
            Field mInstance = singletonClass.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            //取出了AMS代理对象，这里的AMS代理对象就是gDefaultValue对象的值
            Object iActivityManager = mInstance.get(singletonValue);

            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                    , new Class<?>[]{iActivityManagerInterface}, new IActivityManagerHandler(iActivityManager));
            //将gDefaultValue对象的值（即上面的iActivityManager对象）设置为（AMS代理对象的）代理对象的值
            mInstance.set(singletonValue, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            //ActivityThread有一个静态方法返回了自己，这里可以获取activityThread对象
            Method currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);

            Field mH = activityThreadClass.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler mHValue = (Handler) mH.get(activityThread); //获取activityThread对象中mH变量的值，其中mH的类型是Handler类型

            Field callback = Handler.class.getDeclaredField("mCallback");
            callback.setAccessible(true);
            callback.set(mHValue, new ActivityCallbackHandler(mHValue)); //将一个自定义的Callback设置给Handler
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
