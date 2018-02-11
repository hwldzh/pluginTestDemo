package com.example.runningh.plugintestdemo;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by RunningH on 2018/2/5.
 */

public class MainApplication extends Application {
    private static Context mContext;
    private AssetManager assetManager;
    private Resources.Theme mTheme;
    private Resources newResource;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base;


        try {
            String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "plugin_demo.apk";

            assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, apkPath);

            Method ensureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks");
            ensureStringBlocks.setAccessible(true);
            ensureStringBlocks.invoke(assetManager);

            Resources superRes = getResources();
            newResource = new Resources(assetManager, superRes.getDisplayMetrics()
                    , superRes.getConfiguration());
            mTheme = newResource.newTheme();
            mTheme.setTo(MainApplication.getContext().getTheme());

            String cachePath = getCacheDir().getAbsolutePath();
            DexClassLoader pluginLoader = new DexClassLoader(apkPath, cachePath, cachePath, getClassLoader());
            LoaderHelper.mergePathFileElement(pluginLoader);

            ActivityHookHelper.hookActivityWithPit();
            ActivityHookHelper.hookHandler();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    @Override
    public Resources getResources() {
        return newResource == null ? super.getResources() : newResource;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
