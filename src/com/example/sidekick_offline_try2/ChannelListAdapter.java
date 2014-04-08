package com.example.sidekick_offline_try2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadimages.ImageLoader;



public class ChannelListAdapter extends BaseExpandableListAdapter{

	 ArrayList<ShowsList> showListItems;
	 String currTime;
	 String actname="ChanAdapter";
	 ArrayAdapter<String> spinnerAdapter;
	 Spinner spinner;
	 ArrayList<String> spinnerContent;
	 Integer mtype;
	 ExpandableListView lv;
	 ImageLoader imgLoader;
	 private List<String> _listDataHeader; // header titles
	 public static ShowsList presentItem; 
	 
	 public static boolean isLoading;
	 private Context mContext;
	 int positionClicked=0;

	 public ChannelListAdapter(Context context,ArrayList<ShowsList> showsListItems) {
		 mContext = context;
		 this.showListItems = showsListItems;
		 imgLoader = new ImageLoader(mContext,0);
		 presentItem = new ShowsList();
//		 options = new DisplayImageOptions.Builder()
//         .showImageOnLoading(R.drawable.images)
//         .showImageForEmptyUri(R.drawable.images)
//         .showImageOnFail(R.drawable.imagefailed)
//         .cacheInMemory(true)
//         .cacheOnDisc(true)
//         .build();
//		   Toast.makeText(mContext, "INSIDE:"+String.valueOf(showListItems.size()), Toast.LENGTH_SHORT).show();

//	        this._listDataHeader = listDataHeader;

//		 spinnerContent = new ArrayList<String>();
// 		 spinnerContent.add("Add Reminder");
//  		 spinnerContent.add("Off");
//  		 spinnerContent.add("On time");
//  		 spinnerContent.add("5 min before");
//  		 spinnerContent.add("15 min before");
//  		 spinnerContent.add("30 min before");
//  		 spinnerContent.add("1 hour before");
//  		spinnerAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerContent);
//        // Specify the layout to use when the list of choices appears
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        // Apply the adapter to the spinner
        
        
        
		 
	 }
	
	//This is called for every item each time notifydatasetchanged is called.
//	@Override
//   public View getView(int position, View convertView, ViewGroup parent) {
//	   final View v;
//	   LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//       if(convertView == null)
//            v = mInflater.inflate(R.layout.list_item, null);
//       else
//       		v = convertView;
//          Log.e("Position is  ",String.valueOf(position));
//          int chid;
//          String chname;
//          final TextView tim = (TextView) v.findViewById(R.id.timing);
//          final TextView lid = (TextView) v.findViewById(R.id.listingid);
//          final TextView show = (TextView) v.findViewById(R.id.sname);
//          final TextView dow = (TextView) v.findViewById(R.id.dow);
//          final Button watch_button = (Button) v.findViewById(R.id.watchlist);
//          final Button rem_button = (Button) v.findViewById(R.id.reminder);
//          
//          String url= "http://sim.in.com/62/a11e827db099dadd97457aa6d8f17c70_t.jpg";
//          int listing_id=0;
//          LinearLayout lin = (LinearLayout)	v.findViewById(R.id.watch);
//          final TextView desc = (TextView) v.findViewById(R.id.desc);
//          ImageView ChannelLogo=(ImageView)v.findViewById(R.id.showLogoChannel);
//          final ImageView showLogo=(ImageView)v.findViewById(R.id.showLogo);
//          final ShowsList tempshow = showListItems.get(position);
//         // LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)ChannelLogo.getLayoutParams();
//         // params.gravity = Gravity.LEFT;
//          //If we do not have the listings, We display only Channel Id and Name
//          Date dateForReminder = null;
//          ShowsList sl = new ShowsList();
//          ProgressBar pBar = (ProgressBar) v.findViewById(R.id.showview);
//          int progress=0;
//          if(showListItems.size()==0){
//      	  		tim.setText("");
//        		show.setText("Not Available");
//        		desc.setText("");
//        		dow.setText("");
//          }
//          else{
//        	  Log.i("showlistitem", "making call to db");
//        	  // Display the shownames and other details for current view.
//        	  if(mtype == 0)
//        		  sl = MainActivity.datasource.retListItems(tempshow.getChannelId(),currTime);
//        	  else 
//        		  sl = tempshow;
//        	  if(sl!=null){
//        	  			try {
//        	  				tim.setText(sl.getTiming());
//        	  			}catch (ParseException e) {
//        	  				e.printStackTrace();
//        	  			}
//        	  			show.setText(sl.getShowName());
//        	  			desc.setText(sl.getDescription());
//        	  			dow.setText(sl.getDOW());
//        	  			url = sl.getImageURL();
//        	  			dateForReminder = sl.getStartTime();
////        	  			lid.setText(String.valueOf(sl.getListingId()));
//        	  			listing_id = sl.getListingId();
//        	  			showListItems.set(position, sl);
//        	  			if(sl.getEndTime()!=null&&sl.getStartTime()!=null){
//        	  				ML.log("NEW World :", "Times are not null");
//        	  				int num = (int) (System.currentTimeMillis()-sl.getStartTime().getTime());
//        	  				int denom = (int) (sl.getEndTime().getTime()-sl.getStartTime().getTime());
//        	  				ML.log("NEW World :", "Numerator : "+num);
//        	  				ML.log("NEW World :", "Denominator : "+denom);
//
//        	  				progress = (num*100/denom);
//        	  			}
//        	  }
//          }
//          
//          if(MainActivity.datasource.isPrograminWL(sl.getProgramId())){
//	             watch_button.setBackgroundResource(R.drawable.remwatchadded);
//          		 watch_button.setText("Watchlist");
//          }
//          else{
//	             watch_button.setBackgroundResource(R.drawable.remwatch);
//          		 watch_button.setText("Add to Watchlist");
//          }
//          if(MainActivity.datasource.isProgramRem(sl.getListingId())){
//        	  rem_button.setBackgroundResource(R.drawable.remwatchadded);
//	          rem_button.setText("Reminder");
//          }
//          else{
//        	  rem_button.setBackgroundResource(R.drawable.remwatch);
//	          rem_button.setText("Add Reminder");
//          }
//          if(!sl.showRem()){
//        	  rem_button.setVisibility(View.GONE);
//        	 
//        	  pBar.setVisibility(View.VISIBLE);
//        	  pBar.setProgress(progress);
//        	  ML.log("NewWorld:", "GONE CALLEd | "+progress);
//          }
//          else{
//        	  rem_button.setVisibility(View.VISIBLE);
//        	  pBar.setVisibility(View.GONE);
//          }
//          ChannelLogo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("drawable/" + "c"+tempshow.getChannelId().toString(), "drawable", mContext.getPackageName())));
//          
//          Log.e("ChannelListAdapter",String.valueOf(positionClicked));
//          //*** SHOW LOGO ****
//          ImageLoader imageLoader;
//          DisplayImageOptions options;
//          options = new DisplayImageOptions.Builder()
//          .showStubImage(R.drawable.images)
//          .showImageForEmptyUrl(R.drawable.images).cacheInMemory()
//          .cacheOnDisc().build();
//          final ProgressBar mProgressBar = new ProgressBar(mContext);
//          imageLoader = ImageLoader.getInstance();
//          imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
//          imageLoader.displayImage(url, showLogo, options,
//                  new ImageLoadingListener() {
//              public void onLoadingComplete() {
//                  mProgressBar.setVisibility(View.INVISIBLE);
//
//              }
//
//              public void onLoadingFailed() {
//                  mProgressBar.setVisibility(View.INVISIBLE);
//              }
//
//              public void onLoadingStarted() {
//                  mProgressBar.setVisibility(View.VISIBLE);
//              }
//          });
//          
//          
//          //** SHOW LOGO ******
//          final String urltosend = url;
//          final int listingtosend = listing_id;
//          final int chidtosend = tempshow.getChannelId();
//          //final String chnametosend = li.getChannelName();
//          ImageButton chanEdit = (ImageButton) v.findViewById(R.id.furtherDetails);
//          chanEdit.setFocusable(false);
//          chanEdit.setOnClickListener(new OnClickListener(){
//         	 
//  			@Override
//  			public void onClick(View arg0) {
//  				if(listingtosend==-1)
//  					return;
//  				// TODO Auto-generated method stub
//  				 Intent  intent = new Intent(mContext,DetailsPage.class);
//  				// intent.putExtra("chname", chnametosend );
//				 intent.putExtra("chid", String.valueOf(chidtosend));
//				 intent.putExtra("listing_id", listingtosend);
//				 intent.putExtra("showname", show.getText());
//				 intent.putExtra("timings", tim.getText());
//				 intent.putExtra("desc", desc.getText());
//				 intent.putExtra("dow", dow.getText());
//				 intent.putExtra("url", urltosend);
//				 ML.log("ChannelListAdapter","Sending : "+chidtosend);
//  			     mContext.startActivity(intent);
//  			}
//          	 
//           });
//          
//          final ShowsList fshow = sl;
//          watch_button.setOnClickListener(new OnClickListener() {
//        	  @Override
//        	  public void onClick(View v) {
//        		  if(watch_button.getText().toString().equals("Add to Watchlist")){
//     	             watch_button.setBackgroundResource(R.drawable.remwatchadded);
//        			  MainActivity.datasource.addNewWatchList(fshow.getProgramId());
//        			  watch_button.setText("Watchlist");
//        		  }
//        		  else{
//     	             watch_button.setBackgroundResource(R.drawable.remwatch);
//            		  watch_button.setText("Add to Watchlist");
//            		  MainActivity.datasource.remWatchList(fshow.getProgramId());
//        		  }
//        	  }
//          });
//          
//          
//          boolean send;
//         final Calendar calForRem = Calendar.getInstance();
//         
//         long ttosend=0;
//         
//         
//        
//         calForRem.setTimeInMillis(System.currentTimeMillis());
//         if(dateForReminder!=null){
//             calForRem.setTimeInMillis(dateForReminder.getTime());
//             send= true;
//             ttosend = dateForReminder.getTime();
//         }
//         else
//        	 send = false;
//        
//         final boolean sendf = send;
//         final long ttosendf = ttosend;
//         //***************** REMINDER ********************************************
//        final String st_time = sl.st_time;
//         rem_button.setOnClickListener(new OnClickListener() {
//        	  @Override
//        	  public void onClick(View v) {
//        		  if(sendf){
//        			  if(rem_button.getText().toString().equals("Add Reminder")){
//        				  Log.v("REMINDER", "time sending" +DateUtil.formatTime(ttosendf));
//        				  rem_button.setBackgroundResource(R.drawable.remwatchadded);
//        				  MainActivity.datasource.addNewReminder(listingtosend,st_time);
//        				  MainActivity.scheduleClient.setAlarmForNotification(calForRem,show.getText().toString(),listingtosend,ttosendf);
//        				  rem_button.setText("Reminder");
//        			  }
//        			  else{
//        				  rem_button.setBackgroundResource(R.drawable.remwatch);
//        				  MainActivity.datasource.remReminder(listingtosend);
//        				  rem_button.setText("Add Reminder");
//        			  }
//        		 }
//        	  }
//          });
//          
//         
//          if(position+1==positionClicked&&positionClicked!=0){
//       	  	lin.setVisibility(TextView.VISIBLE);
//       	  	v.setBackgroundColor(0xFFF0F0F0);
//          }
//          else{
//       	   	lin.setVisibility(TextView.GONE);
//       	   	v.setBackgroundColor(0xFFFFFFFF);
//          }
//         
//       	  
//       return v;
//   }

	public void reload(ArrayList<ShowsList> showsList,String whattosend){
		showListItems.clear();
		showListItems.addAll(showsList);
		currTime = whattosend;
	}
	
	public void reload(ArrayList<ShowsList> showsList){
		showListItems.clear();
		showListItems.addAll(showsList);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		View v;
		if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = infalInflater.inflate(R.layout.list_expandableitem, null);
        }
		else
			v = convertView;
		v.setBackgroundColor(0xFFF0F0F0);
		
		final Button watch_button = (Button) v.findViewById(R.id.watchlist);
		final Button rem_button = (Button) v.findViewById(R.id.reminder);
		final TextView desc = (TextView) v.findViewById(R.id.synopsis);
		final TextView episode_synop = (TextView) v.findViewById(R.id.episode_synopsis);

		final ShowsList showItem = showListItems.get(groupPosition);
		final Date dateForReminder =showItem.getStartTime();
		
		final int listingId = showItem.getListingId();
		
		
		if(!showItem.getDescription().equals("null"))
		desc.setText(showItem.getDescription());
		
		if(!showItem.getEpisodeDescription().equals("null"))
			episode_synop.setText(showItem.getEpisodeDescription());
		
		
		   if(MainActivity.datasource.isPrograminWL(showItem.getProgramId())){
			  watch_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
//	          watch_button.setBackgroundResource(R.drawable.remwatchadded);
       		  watch_button.setText("Watchlist");
		   }
	       else{
				  watch_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//		     watch_button.setBackgroundResource(R.drawable.remwatch);
	       	 watch_button.setText("Add to Watchlist");
	       }
	       if(MainActivity.datasource.isProgramRem(showItem.getListingId())){
	    	  rem_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
//	     	  rem_button.setBackgroundResource(R.drawable.remwatchadded);
		      rem_button.setText("Reminder");
	       }
	       else{
		    	  rem_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//	     	  rem_button.setBackgroundResource(R.drawable.remwatch);
		      rem_button.setText("Add Reminder");
	       }
	       
	       
	       
	       
	       
	       if(!showItem.showRem())
	     	  rem_button.setVisibility(View.GONE);
	       else
	     	  rem_button.setVisibility(View.VISIBLE);
	       
	       
	       
	       
	       watch_button.setOnClickListener(new OnClickListener() {
	        	  @Override
	        	  public void onClick(View v) {
	        		  if(watch_button.getText().toString().equals("Add to Watchlist")){
	        			  watch_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
	        			  MainActivity.datasource.addNewWatchList(showItem.getProgramId());
	        			  watch_button.setText("Watchlist");
	        		  }
	        		  else{
	        			  watch_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	            		  watch_button.setText("Add to Watchlist");
	            		  MainActivity.datasource.remWatchList(showItem.getProgramId());
	        		  }
	        	  }
	       });
	          
	          
	         boolean send;
//	         final Calendar calForRem = Calendar.getInstance();
	         long ttosend=0;
//	         calForRem.setTimeInMillis(System.currentTimeMillis());
	         
	         if(dateForReminder!=null){
//	             calForRem.setTimeInMillis(dateForReminder.getTime());
	             send= true;
//	             ttosend = dateForReminder.getTime();
	         }
	         else
	        	 send = false;
	        
	         final boolean sendf = send;
//	         final long ttosendf = ttosend;
	         //***************** REMINDER ********************************************
	        final String st_time = showItem.st_time;
	         rem_button.setOnClickListener(new OnClickListener() {
	        	  @Override
	        	  public void onClick(View v) {
	        		  if(sendf){
	        			  if(rem_button.getText().toString().equals("Add Reminder")){
	        				  Log.v("REMINDER", "time sending" +DateUtil.formatTime(dateForReminder.getTime()));
	        				  rem_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
	        				  MainActivity.datasource.addNewReminder(listingId,st_time);
	        				  
	        				  
	        				  AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	        			      Intent notificationIntent = new Intent(mContext, NotificationReminder.class);
	        			      notificationIntent.putExtra("Name",showItem.getShowName() );
	        			      notificationIntent.putExtra("Description","Your favourite show is about to start ..." );
	        			      notificationIntent.putExtra("NotifyId",listingId);
	        			         
	        			      PendingIntent pi = PendingIntent.getBroadcast(mContext, listingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        			      mgr.set(AlarmManager.RTC_WAKEUP,dateForReminder.getTime(), pi);
	        			      
	        			      Toast.makeText(mContext, "Your Reminder Activated", Toast.LENGTH_LONG).show();
	        				  rem_button.setText("Reminder");
	        			  }
	        			  else{
//	        				  MainActivity.scheduleClient.resetAlarmForNotification(calForRem,showItem);
	        				  rem_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	        				  MainActivity.datasource.remReminder(listingId);
	        				  rem_button.setText("Add Reminder");
	        			  }
	        		 }
	        	  }
	          });
		return v;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return 1;
	}


	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return showListItems.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final View v;

		   LayoutInflater mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	       if(convertView == null)
	            v = mInflater.inflate(R.layout.list_group, null);
	       else
	       		v = convertView;
	       
	          ProgressBar loader = (ProgressBar) v.findViewById(R.id.showLogoLoader);
	          
	          final TextView tim = (TextView) v.findViewById(R.id.timing);
	          final TextView lid = (TextView) v.findViewById(R.id.listingid);
	          final TextView show = (TextView) v.findViewById(R.id.sname);
//	          final TextView cname = (TextView) v.findViewById(R.id.cname);
	          final TextView dow = (TextView) v.findViewById(R.id.dow);
	          
//	          Typeface face = Typeface.createFromAsset(mContext.getAssets(),
//	                      "RobotoCondensed-Regular.ttf");
//	          tim.setTypeface(face);
//	          dow.setTypeface(face);
//              show.setTypeface(face);
	         
	          if(isExpanded)
	        	  v.setBackgroundColor(0xFFF0F0F0);
	          else
	        	  v.setBackgroundColor(0xFFFFFFFF);
	          
	          
	          ImageView ChannelLogo=(ImageView)v.findViewById(R.id.showLogoChannel);
	          final ImageView showLogo=(ImageView)v.findViewById(R.id.showLogo);
	          final ShowsList showItem = showListItems.get(groupPosition);
	        	  
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
	          
	          
	          ImageButton chanEdit = (ImageButton) v.findViewById(R.id.furtherDetails);
	          chanEdit.setFocusable(false);
	          chanEdit.setOnClickListener(new OnClickListener(){
	         	 
	  			@Override
	  			public void onClick(View arg0) {
	  				if(listingId==-1)
	  					return;
	  				// TODO Auto-generated method stub
	  				 Intent  intent = new Intent(mContext,DetailsPage.class);
	  				// intent.putExtra("chname", chnametosend );
	  				 presentItem = showItem;
//					 intent.putExtra("chid", channelId);
//					 intent.putExtra("listing_id", listingId);
//					 intent.putExtra("showname", showName);
//					 intent.putExtra("synopis", progSynopsis);
//					 intent.putExtra("episode_synopsis", epiSynopsis);
//					 intent.putExtra("timings", tim.getText());
//					 intent.putExtra("dow", dayOfWeek);
//					 intent.putExtra("url", showLogoUrl);
	  			     mContext.startActivity(intent);
	  			}
	           });
	          
	       return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}
}


