package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
 

 
	public class GridAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
	    private ArrayList<String> totalChanNameList;
	    public static ArrayList<Integer> totalChanIds;
	    public static ArrayList<Integer> selectedChanIds;
    
		public GridAdapter(Context context, ArrayList<Integer> totalChanIds,ArrayList<Integer> selectedChanIds) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			GridAdapter.totalChanIds = new ArrayList<Integer>();
			GridAdapter.selectedChanIds = new ArrayList<Integer>();
			this.totalChanNameList = new ArrayList<String>();
			
			GridAdapter.totalChanIds = totalChanIds;
			GridAdapter.selectedChanIds = selectedChanIds;
			this.totalChanNameList=MainActivity.datasource.getChannel_NameList(totalChanIds);
		}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		View v;
		if(convertView == null) {
            v = mInflater.inflate(R.layout.gridelement, null);
        } else {
            v = convertView;
        }
		
		Integer thisChannelId = totalChanIds.get(position);

		
		ImageView channelLogo=(ImageView)v.findViewById(R.id.channelLogo);
		TextView channelName = (TextView) v.findViewById(R.id.editChannelName);
		CheckBox checkBox = (CheckBox)v.findViewById(R.id.editChannelCheck);
		
		channelName.setText(String.valueOf(totalChanNameList.get(position)));
		
		
//		 Integer decFinId;
//         if(thisChannelId > 254)
//       	  decFinId = 254;
//         else
//       	  decFinId = thisChannelId;
		
		try{
			channelLogo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("drawable/" + "c"+thisChannelId, "drawable", mContext.getPackageName())));
        }catch(Exception e){
        	channelLogo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("drawable/" + "imagefailed", "drawable", mContext.getPackageName())));
        }
		
		if(selectedChanIds.contains(thisChannelId))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		
		final int finPos = position;
		
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(selectedChanIds.contains(totalChanIds.get(finPos)))
					selectedChanIds.set(finPos,-1);
				else
					selectedChanIds.set(finPos,totalChanIds.get(finPos));
				
				notifyDataSetChanged();
			}
		});
		
//			CheckBox ch = (CheckBox) v.findViewById(R.id.chanvis);
//			final Category cat;
//			if(pos < MainActivity.categories.size()){
//				 cat= MainActivity.categories.get(pos);
//				 if(cat.returnAsIntArray().contains(chanIds.get(position)))
//					 ch.setChecked(true);
//			}
//			else
//			ch.setChecked(false);
//			Log.e("lengthof WOC ",String.valueOf(EditChannelActivity.whichOneIsClicked.size())+",||"+textView.getText());

		return v;
	}
 
	@Override
	public int getCount() {
		return totalChanIds.size();
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
 
}