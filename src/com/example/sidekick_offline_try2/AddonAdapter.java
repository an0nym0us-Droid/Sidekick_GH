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
 

 
public class AddonAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
    private ArrayList<String> totalChanNameList;
    public static ArrayList<String> totalAddons;
    public static ArrayList<String> selectedAddons;
    
	public AddonAdapter(Context context, ArrayList<String> totalAddons,ArrayList<String> selectedAddons) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		AddonAdapter.totalAddons = new ArrayList<String>();
		AddonAdapter.selectedAddons = new ArrayList<String>();
		
		AddonAdapter.totalAddons = totalAddons;
		AddonAdapter.selectedAddons = selectedAddons;
	}
 
	public void refreshTotalAddons(ArrayList<String> totalAddons){
		AddonAdapter.totalAddons.clear();
		AddonAdapter.totalAddons = totalAddons;
		AddonAdapter.selectedAddons.clear();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
 
		View v;
		if(convertView == null) {
            v = mInflater.inflate(R.layout.addonelement, null);
        } else {
            v = convertView;
        }
		
		String thisAddon = totalAddons.get(position);
		TextView addonName = (TextView) v.findViewById(R.id.addonName);
		CheckBox checkBox = (CheckBox)v.findViewById(R.id.addonCheck);
		
		addonName.setText(String.valueOf(totalAddons.get(position)));
		
		
//		 Integer decFinId;
//         if(thisChannelId > 254)
//       	  decFinId = 254;
//         else
//       	  decFinId = thisChannelId;
		
		
		if(selectedAddons.contains(thisAddon))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		
		final int finPos = position;
		
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(selectedAddons.contains(totalAddons.get(finPos)))
					selectedAddons.remove(totalAddons.get(finPos));
				else
					selectedAddons.add(totalAddons.get(finPos));
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
		return totalAddons.size();
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