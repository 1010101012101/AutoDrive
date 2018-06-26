package com.icegps.autodrive.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.icegps.autodrive.App;


/**
 * Created by 111 on 2017/11/28.
 */

public class Sp {

    private static Sp instance;
    private static SharedPreferences sharedPreferences;

    private Sp() {

    }

    public static Sp getInstance() {
        if (instance == null) {
            synchronized (Sp.class) {
                if (instance == null) {
                    instance = new Sp();
                    sharedPreferences =Init.context.getSharedPreferences("himap", Context.MODE_PRIVATE);
                }
            }
        }
        return instance;
    }

    public void putString(String key, String value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getString(String key) {
        if (sharedPreferences == null) return "";
        return sharedPreferences.getString(key, "");
    }

    public boolean getBoolean(String key) {
        if (sharedPreferences == null) return false;
        return sharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(String key, boolean value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void putFloat(String key, float value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public float getFloat(String key) {
        if (sharedPreferences == null) return 0;
        return sharedPreferences.getFloat(key, 0);
    }

    public void putWorkWidth(String key, float value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public float getWorkWidth(String key) {
        if (sharedPreferences == null) return -1;
        return sharedPreferences.getFloat(key, -1);
    }

    public float getLong(String key) {
        if (sharedPreferences == null) return 0;
        return sharedPreferences.getFloat(key, 0);
    }

    public void putInt(String key, int value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public int getInt(String key) {
        if (sharedPreferences == null) return 1;
        return sharedPreferences.getInt(key, 1);
    }

    public void clear() {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        boolean commit = editor.commit();
    }
}
