package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class MyAdapter extends ArrayAdapter<String>{
     private Context mContext;
     private LayoutInflater mInflater;
     private ArrayList<String> items;
     int id;
     public MyAdapter(Context c,int resource,ArrayList<String> list) {
    	 super(c,resource,list);
         mContext = c;
         mInflater = LayoutInflater.from(mContext);
         items = list;
         id = resource;
     }


     public View getView(int position, View convertView, ViewGroup parent) {
         final View v;
             v = mInflater.inflate(R.layout.catitem, null);
         
             int k=0;
         Category temp_cat = new Category();
     	 for(Category item : MainActivity.categories){ 
     			if(item.retVisibility()==1){
     				if(k==position){
     					temp_cat = item;
     					break;
     				}
     				else
     					k++;
     			}
     	 }    
//         Log.e("Sidekick_V3","currCat:"+MainActivity.currCatIdSelected+"|"+MainActivity.categories.get(position).retId());   
         if(temp_cat.retId()==MainActivity.currCatIdSelected)
        	 v.setBackgroundResource(R.drawable.pressed_color);
         else
        	 v.setBackgroundResource(R.drawable.normal_color);
         
         TextView catname = (TextView)v.findViewById(R.id.catname);
         catname.setText(items.get(position));
         return v;
     }
 
}
