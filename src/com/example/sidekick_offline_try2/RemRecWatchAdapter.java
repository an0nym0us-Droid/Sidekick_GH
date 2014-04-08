package com.example.sidekick_offline_try2;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.loadimages.ImageLoader;




public class RemRecWatchAdapter extends BaseAdapter{

	 ArrayList<ShowsList> showListItems;
	 String actname="ChanAdapter";
	 ImageLoader imgLoader;

	 private Context mContext;
	 int positionClicked=0;

	 public RemRecWatchAdapter(Context context,ArrayList<ShowsList> showItems) {
		 mContext = context;
		 this.showListItems = showItems;
		 imgLoader = new ImageLoader(mContext,0);

	 }
	
	//This is called for every item each time notifydatasetchanged is called.
	@Override
   public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		 LayoutInflater mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	       if(convertView == null)
	            v = mInflater.inflate(R.layout.list_group, null);
	       else
	       		v = convertView;
	       
	          ProgressBar loader = (ProgressBar) v.findViewById(R.id.showLogoLoader);
	          
	          final TextView tim = (TextView) v.findViewById(R.id.timing);
	          final TextView lid = (TextView) v.findViewById(R.id.listingid);
	          final TextView show = (TextView) v.findViewById(R.id.sname);
	          final TextView dow = (TextView) v.findViewById(R.id.dow);
	          
	         
	          
	          
	          ImageView ChannelLogo=(ImageView)v.findViewById(R.id.showLogoChannel);
	          final ImageView showLogo=(ImageView)v.findViewById(R.id.showLogo);
	          final ShowsList showItem = showListItems.get(position);
	        	  
	          ProgressBar pBar = (ProgressBar) v.findViewById(R.id.showview);
	          int progress=0;
	          
	          final int channelId = showItem.getChannelId();
	          final int listingId = showItem.getListingId();
	          final String showLogoUrl = showItem.getImageURL();
	          final String showName = showItem.getShowName();
	          final String dayOfWeek = showItem.getDOW();
	          final String progSynopsis = showItem.getDescription();
	          final String epiSynopsis = showItem.getEpisodeDescription();
	          
  	  			try {
  	  				tim.setText(showItem.getTiming());
  	  			}catch (ParseException e) {
  	  				e.printStackTrace();
  	  			}
  	  			show.setText(showName);
  	  			dow.setText(dayOfWeek);
  	  			lid.setText(String.valueOf(listingId));
  	  			
  	  			if(showItem.getEndTime()!=null&&showItem.getStartTime()!=null){
  	  				int num = (int) (System.currentTimeMillis()-showItem.getStartTime().getTime());
  	  				int denom = (int) (showItem.getEndTime().getTime()-showItem.getStartTime().getTime());
  	  				progress = (num*100/denom);
  	  			}
	          
	          if(!showItem.showRem()){
	        	  pBar.setVisibility(View.VISIBLE);
	        	  pBar.setProgress(progress);
	          }
	          else
	        	  pBar.setVisibility(View.GONE);
	          
	          try{
	        	  ChannelLogo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("drawable/" + "c"+showItem.getChannelId(), "drawable", mContext.getPackageName())));
	          }catch(Exception e){
	        	  ChannelLogo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("drawable/" + "imagefailed", "drawable", mContext.getPackageName())));
	          }
	          
	          imgLoader.DisplayImage(showLogoUrl, loader, showLogo, R.drawable.transparent);
	          
	          return v;
   }

	public void reload(ArrayList<ShowsList> showsList){
		showListItems.clear();
		showListItems.addAll(showsList);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

}