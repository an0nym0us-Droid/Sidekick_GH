package com.example.loadimages;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

 
public class MemoryCache {
   // private Map<String, LruCache<Bitmap>> cache=Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;
    
    
    public MemoryCache(){
    	 mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
             @Override
             protected int sizeOf(String key, Bitmap bitmap) {
                 // The cache size will be measured in kilobytes rather than
                 // number of items.
                 return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
             }
         };
    }
    public Bitmap get(String id){
        Log.e("CACHE","RETURNING NULL");
        return mMemoryCache.get(id);
    }
 
    public void put(String id, Bitmap bitmap){
        mMemoryCache.put(id, bitmap);
    }
 
    public void clear() {
        mMemoryCache.evictAll();
    }
}
