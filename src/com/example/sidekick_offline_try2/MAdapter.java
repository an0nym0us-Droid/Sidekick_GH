package com.example.sidekick_offline_try2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;



public class MAdapter extends BaseAdapter {
     private Context mContext;
     private LayoutInflater mInflater;
     
     public MAdapter(Context c) {
         mContext = c;
         mInflater = LayoutInflater.from(mContext);
     }

     public int getCount() {
         return MainActivity.categories.size()-1;
     }
     
     public Object getItem(int position) {
         return MainActivity.categories.get(position+1);
     }

     public long getItemId(int position) {
         return position-1;
     }
     
     public void show(){
    	 StringBuilder build = new StringBuilder();
         for(Category item : MainActivity.categories)
         	build.append(item.retCategoryName()+"\n");
         Log.e("ORDER",build.toString());
     } 

     public View getView(int position, View convertView, ViewGroup parent) {
         final View v;
         if(convertView == null) {
             v = mInflater.inflate(R.layout.fin_editcatitem, null);
         } else {
             v = convertView;
         }
         final int finPos = position+1;
         
         final TextView catName = (TextView)v.findViewById(R.id.fincatname);
         ImageButton catVisibility = (ImageButton) v.findViewById(R.id.catVisibility);
         if(MainActivity.categories.get(finPos).retVisibility()==1){
				catVisibility.setImageResource(R.drawable.visible);
         }else{
				catVisibility.setImageResource(R.drawable.invisible);
         }
         catVisibility.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					int vis = (MainActivity.categories.get(finPos).retVisibility()^1);
					if(vis==1){
						((ImageButton)v).setImageResource(R.drawable.visible);
					}else{
						((ImageButton)v).setImageResource(R.drawable.invisible);
					}
					MainActivity.categories.get(finPos).setVisibility(vis);
					
			}
		});
         catName.setText(MainActivity.categories.get(position+1).retCategoryName());
         catName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				 Intent  intent = new Intent(mContext,EditChannelActivity.class);
				 intent.putExtra("catName", catName.getText() );
				 intent.putExtra("catPos", finPos );
			     mContext.startActivity(intent);
			}
         });
//         Log.i("Enetered", "1");
//         final Category temp = cats.get(position+1);
//         final int pos = position;
//         String item = mStrings.get(position);
//         Log.i("Enetered", "2");
//         //ToggleButton visi = (ToggleButton) v.findViewById(R.id.togVisible);
////         ImageButton chanEdit = (ImageButton) v.findViewById(R.id.drag_handle1);
//         Log.i("Entered", "3");
////         chanEdit.setOnClickListener(new OnClickListener(){
//        	 
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				 Intent  intent = new Intent(mContext,EditChannelActivity.class);
//				 intent.putExtra("catName", temp.retCategoryName() );
//				 intent.putExtra("catPos", pos+1 );
//				 intent.putExtra("newlyCreated", false);
//				 Log.i("onclick", ""+pos+temp.retCategoryName());
//			     mContext.startActivity(intent);
//			}
//        	 
//         });
//         final TextView name = (TextView)v.findViewById(R.id.text1);
//         name.setText(item);
         return v;
     }
 
}
