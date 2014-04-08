package com.example.loadimages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.sidekick_offline_try2.MainActivity;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
 
public class ImageLoader {
 
    FileCache fileCache;
    MemoryCache memoryCache;
    int size;
    int transparent;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
 
    public ImageLoader(Context context, int size){
        fileCache=new FileCache(context);
        memoryCache = new MemoryCache();
        this.size = size;
        executorService=Executors.newFixedThreadPool(6);
    }
 
    public void DisplayImage(String url, ProgressBar loader, ImageView imageView,int t)
    {
    	this.transparent = t;
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            loader.setVisibility(View.GONE);
            Log.e("CACHE","GETTING IMAGE FROM CACHE");
        }
        else{
            Log.e("CACHE","BITMAP WAS NULL");
            imageView.setImageResource(transparent);
            queuePhoto(url, imageView,loader);
            loader.setVisibility(View.VISIBLE); 
        }
    }
 
    private void queuePhoto(String url, ImageView imageView, ProgressBar loader)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView,loader);
        executorService.submit(new PhotosLoader(p));
    }
 
    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);
 
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
 
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }
 
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
 
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=150;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
 
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            if(size==0)
            	return Bitmap.createScaledBitmap(bmp, (int)(95*MainActivity.factor), (int)(MainActivity.factor*85), true);
            else
            	return Bitmap.createScaledBitmap(bmp, (int)(140*MainActivity.factor), (int)(MainActivity.factor*140), true);

        } catch (FileNotFoundException e) {}
        return null;
    }
 
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public ProgressBar loader;
        public PhotoToLoad(String u, ImageView i,ProgressBar l){
            url=u;
            imageView=i;
            loader=l;
        }
    }
 
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
 
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
 
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
 
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.loader.setVisibility(View.GONE);
            	photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else{
                photoToLoad.loader.setVisibility(View.VISIBLE);
            }
        }
    }
 
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
 
}
