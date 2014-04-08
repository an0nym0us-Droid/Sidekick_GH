package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
 
public class SetupwizardGridAdapter extends BaseAdapter {
	private Context context;
	ArrayList<Channels> channels;
	String category;
	
	public SetupwizardGridAdapter(Context context, ArrayList<Channels> particularChannels) {
		this.context = context;
		this.channels = particularChannels;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
 
			gridView = new View(context);
			
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.grid_setup_category, null);
			
			Channels chan = channels.get(position);
			LinearLayout l = (LinearLayout) gridView.findViewById(R.id.setupGridOuterboundary);
			ImageView im = (ImageView) 	gridView.findViewById(R.id.setup_grid_check);
			//Checking if it should be checked or not
			if(TryingExpandable.selectedChannelIds.contains(chan.retId())){
				im.setVisibility(View.VISIBLE);
				l.setBackgroundColor(0xAAED9A00);
			}else{
				im.setVisibility(View.GONE);
				l.setBackgroundColor(0x00000000);
			}
			
			ImageView gridSetupImage = (ImageView) gridView.findViewById(R.id.gridSetupImage);
			TextView gridSetupTv = (TextView) gridView.findViewById(R.id.gridSetupName);
			try{
				gridSetupImage.setImageDrawable(context.getResources().getDrawable(context.getResources().getIdentifier("drawable/" + "c"+chan.retId(), "drawable", context.getPackageName())));
			}catch(Exception e){
				gridSetupImage.setImageDrawable(context.getResources().getDrawable(context.getResources().getIdentifier("drawable/" + "imagefailed", "drawable", context.getPackageName())));
			}
			
			gridSetupTv.setText(chan.retChannelName());
			
			// set value into textview
			
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return channels.size();
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
