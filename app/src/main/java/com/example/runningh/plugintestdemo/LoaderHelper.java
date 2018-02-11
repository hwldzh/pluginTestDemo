package com.example.runningh.plugintestdemo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Created by RunningH on 2018/2/5.
 */

public class LoaderHelper {

    public static void mergePathFileElement(ClassLoader pluginLoader) {
        ClassLoader originPathLoader = MainApplication.getContext().getClassLoader();
        Object originPathList = getPathList(originPathLoader); //拿到了BaseDexClassLoader的字段"pathList"的值，其实是一个DexPathList类型的对象
        Object pluginPathList = getPathList(pluginLoader);
        Object originElements = getElement(originPathList);
        Object pluginElements = getElement(pluginPathList);
        Object combineElements = combineElements(originElements, pluginElements);
        //将合并的Elements设置给原应用
        setDexElements(originPathList, combineElements);
    }

    private static Object getPathList(ClassLoader loader) {
        try {
            Class<?> baseLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathList = baseLoaderClass.getDeclaredField("pathList");
            pathList.setAccessible(true);
            Object o = pathList.get(loader);
            return o;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getElement(Object o) {
        if (o == null) {
            return null;
        }
        Class<?> PathListClass = o.getClass();
        try {
            Field dexElementsField = PathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object elementObj = dexElementsField.get(o);
            return elementObj;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object combineElements(Object originElements, Object pluginElements) {
        Class<?> arrayType = originElements.getClass().getComponentType();
        int originLength = Array.getLength(originElements);
        int pluginLength = Array.getLength(pluginElements);
        int lengths = originLength + pluginLength;
        Object newArray = Array.newInstance(arrayType, lengths);
        for (int i = 0; i < lengths; i++) {
            if (i < originLength) {
                Array.set(newArray, i, Array.get(originElements, i));
            } else {
                Array.set(newArray, i, Array.get(pluginElements, i - originLength));
            }
        }
        return newArray;
    }

    private static void setDexElements(Object originPathList, Object combineElements) {
        try {
            Field dexElementsField = originPathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            dexElementsField.set(originPathList, combineElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
