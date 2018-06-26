package icegps.com.maplib.map.mapdata;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;


import java.util.HashSet;


/**
 * Created by 111 on 2018/3/30.
 */

public class BitmapLruCache extends LruCache<String, Bitmap> {

    private HashSet<String> keys;

    public BitmapLruCache(int maxSize) {
        super(maxSize);
        keys = new HashSet<>();
    }

    public HashSet<String> getKeys() {
        return keys;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount();
    }


    public void add(String key, Bitmap bitmap) {
        put(key, bitmap);
        keys.add(key);
    }



}
