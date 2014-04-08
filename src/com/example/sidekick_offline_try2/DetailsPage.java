package com.example.sidekick_offline_try2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.loadimages.ImageLoader;
import com.facebook.Session;



public class DetailsPage extends SherlockActivity {

	String actname="DetailsPage";
	CheckBox shareFbCheckbox ;
	SharedPreferences myPreferences;
	SharedPreferences.Editor editor;
	ShowsList showClicked;
	ImageLoader imageLoader;
	boolean shareOnFb;
	boolean isTunedIn;
	boolean isReminder;
	ActionBar actionBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_page);
		
		myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = myPreferences.edit();
		imageLoader = new ImageLoader(this,1);
		
		
		actionBar = getSupportActionBar();
		actionBar.setTitle("\tAbout the Show");
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		showClicked = new ShowsList();
		showClicked = ChannelListAdapter.presentItem;
		Toast.makeText(this,showClicked.getShowName(),Toast.LENGTH_SHORT).show();
        final TextView timingTV = (TextView) findViewById(R.id.detailTiming);
        final TextView dowTV = (TextView) findViewById(R.id.detailDOW);
        final TextView shownameTV = (TextView) findViewById(R.id.detailShowName);
        final TextView synopsisTV = (TextView) findViewById(R.id.detailShowSynopsis);
        final TextView episodesynopsisTV = (TextView) findViewById(R.id.detailEpisodeSynopsis);

        final TextView repeatTimingTV = (TextView) findViewById(R.id.repeatTiming);
        final TextView repeatdowTV = (TextView) findViewById(R.id.repeatDOW);
        final TextView repeatHeading = (TextView) findViewById(R.id.repeatHeading);
        
        
        ImageView showLogo = (ImageView) findViewById(R.id.detailShowLogo);
        ProgressBar loader = (ProgressBar) findViewById(R.id.detailShowLogoLoader);
        
		Button shareTuneIn = (Button) findViewById(R.id.shareTunein);
		Button setReminder = (Button) findViewById(R.id.setReminder);
		Button prevEpi = (Button) findViewById(R.id.prevEpisodes);
		
		if(showClicked.getYoutube().equals(""))
			prevEpi.setVisibility(View.GONE);
		shareFbCheckbox = (CheckBox) findViewById(R.id.shareOnFb);

		if(!showClicked.showRem())
	     	  setReminder.setVisibility(View.GONE);
	       else
	     	  setReminder.setVisibility(View.VISIBLE);
		
		 if(MainActivity.datasource.isProgramTunedin(showClicked.getListingId())){
			  shareTuneIn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
      		  shareTuneIn.setText("Tuned In");
      		  isTunedIn = true;
		 }
	     else{
	    	 shareTuneIn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detailtunein, 0, 0, 0);
     		  shareTuneIn.setText("Tune In");
     		  isTunedIn = false;
	     }
	     
	     if(MainActivity.datasource.isProgramRem(showClicked.getListingId())){
	    	  setReminder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
	    	  setReminder.setText("Reminder");
	    	  isReminder=true;
	     }
	     else{
	    	 isReminder=false;
	    	  setReminder.setText("Set Reminder");

	    	 setReminder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detailreminder, 0, 0, 0);
	     }
//		isTunedIn = false;
		
//		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View v = mInflater.inflate(R.layout.pref, null);
//	    final CheckBox pubperm = (CheckBox) v.findViewById(R.id.share);
		
	    final Session session = Session.getActiveSession();
//	    if (session != null && session.isOpened()) {
//			fbTunein.setChecked(true);
//		}
//	    else
//	    	fbTunein.setChecked(false);
		shareFbCheckbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(shareFbCheckbox.isChecked()){
					TextView tv = new TextView(DetailsPage.this);
					tv.setPadding(10, 10, 10, 10);
					if(myPreferences.getString("fbid", "").equals("")){
						tv.setText("To share your favourite shows on facebook please login to Sidekick with your facebook id.");
					}
					else if(!session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream"))){
						tv.setText("Please provide publish permissions in order to share your activities on facebook");
					}
					else
						return;
					
					AlertDialog.Builder alert = new AlertDialog.Builder(DetailsPage.this);
					alert.setTitle("Share on Facebook");
					alert.setView(tv);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Intent intent = new Intent(DetailsPage.this,Settings.class);
							startActivity(intent);
						  }
						});

						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
						    // Canceled.
							  shareFbCheckbox.setChecked(false);
						  }
						});
						alert.show();
				}
			}
		});
	    
		
		
		if(showClicked.getRetelecast()==null|showClicked.getRetelecast().equals("")){
			repeatHeading.setVisibility(View.GONE);
			repeatdowTV.setVisibility(View.GONE);
			repeatTimingTV.setVisibility(View.GONE);
		}else{
			repeatdowTV.setText(showClicked.getRepeatDOW());
			try{
				repeatTimingTV.setText(showClicked.getRepeatTiming());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        shownameTV.setText(showClicked.getShowName());
        dowTV.setText(showClicked.getDOW());
        
        try {
        	
			timingTV.setText(showClicked.getTiming());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        	try {
        		if(!showClicked.getRepeatTiming().equals(""))
				repeatTimingTV.setText(showClicked.getRepeatTiming());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        if(showClicked.getDescription().length()>=10)
        	synopsisTV.setText(showClicked.getDescription());
        else
            synopsisTV.setText("");

        if(showClicked.getEpisodeDescription().length()>=10)
        	episodesynopsisTV.setText(showClicked.getEpisodeDescription());
        else
        	episodesynopsisTV.setText("");

        
        
        imageLoader.DisplayImage(showClicked.getImageURL(), loader, showLogo, R.drawable.transparent);
	}
	
	public void watchPrev(View v){
		Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse(showClicked.getYoutube()));
		startActivity(send);
	}
	
	public void shareTuneIn(View v){
		Button tuneInButton = ((Button) v);
		isTunedIn=!isTunedIn;
		if(isTunedIn){
			tuneInButton.setText("Tuned in");
			tuneInButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
			MainActivity.datasource.addNewTunein(showClicked.getListingId(),showClicked.st_time);
			isTunedIn = true;
			//Add to tune in to db;
				if(!shareFbCheckbox.isChecked()){
					//TODO : SEND to server only without fbdata
				}
				else{
					StringBuilder builder = new StringBuilder();
					final JSONObject json = new JSONObject();
			        String msg;
					
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("Share on Facebook");
		
					// Set an EditText view to get user input 
					final EditText input = new EditText(this);
					input.setHint("I'm watching "+showClicked.getShowName());
					alert.setView(input);
		
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							try {
								String msg = input.getText().toString();
								json.put("fb_id", myPreferences.getString("fbid", ""));
								json.put("device_key", myPreferences.getString("devicekey", ""));
								json.put("request_time", DateUtil.formatTime(System.currentTimeMillis()));
								json.put("listing_id", showClicked.getListingId());
								if(msg.equals(""))
									msg = "I'm watching "+showClicked.getShowName();
								json.put("message", msg );
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							new DownloadFilesTask().execute(new String[] {json.toString()});
					  }
					});
					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
					  }
					});
					alert.show();
				}
		}
		else{
			isTunedIn=false;
			tuneInButton.setText("Tune in");
			MainActivity.datasource.remTunein(showClicked.getListingId());
			tuneInButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detailtunein, 0, 0, 0);
		}
	}
	
	public void setReminder(View v){
		Button reminderButton = ((Button) v);
		isReminder=!isReminder;
		if(isReminder){
			reminderButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_icon, 0, 0, 0);
			MainActivity.datasource.addNewReminder(showClicked.getListingId(),showClicked.st_time);
			reminderButton.setText("Reminder");  
			  
			  AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		      Intent notificationIntent = new Intent(this, NotificationReminder.class);
		      notificationIntent.putExtra("Name",showClicked.getShowName() );
		      notificationIntent.putExtra("Description","Your favourite show is about to start ..." );
		      notificationIntent.putExtra("NotifyId",showClicked.getListingId());
		         
		      PendingIntent pi = PendingIntent.getBroadcast(this, showClicked.getListingId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		      mgr.set(AlarmManager.RTC_WAKEUP,showClicked.getStartTime().getTime(), pi);
		      
		      Toast.makeText(this, "Your Reminder Activated", Toast.LENGTH_LONG).show();
		}else{
			reminderButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.detailreminder, 0, 0, 0);
			AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		      Intent notificationIntent = new Intent(this, NotificationReminder.class);
		      notificationIntent.putExtra("Name",showClicked.getShowName() );
		      notificationIntent.putExtra("Description","Your favourite show is about to start ..." );
		      notificationIntent.putExtra("NotifyId",showClicked.getListingId());
		      PendingIntent pi = PendingIntent.getBroadcast(this, showClicked.getListingId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		      mgr.cancel(pi);
		      Toast.makeText(this, "Your Reminder has been removed", Toast.LENGTH_LONG).show();
		      MainActivity.datasource.remReminder(showClicked.getListingId());
			reminderButton.setText("Set Reminder");
		}
	}
	
	private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {
	       
		@Override
		protected Void doInBackground(String... params) {
			ML.log(actname, "doInBackground");
        	String jsonString = params[0];
        	String uri = getResources().getString(R.string.domain)+"fb_status/create";
        	StringBuilder builder = new StringBuilder();
        	HttpClient httpclient = new DefaultHttpClient();
     	    HttpPost httppost = new HttpPost(uri);
     	    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
     	   	postParams.add(new BasicNameValuePair("fb_status_json", jsonString));
     	    ML.log(actname,"String to send : "+jsonString);
     	       try{
     	    	   	UrlEncodedFormEntity urlentity = new UrlEncodedFormEntity(postParams);
     	    	    urlentity.setContentEncoding(HTTP.UTF_8);
     	    	    httppost.setEntity(urlentity);
     	    	//  httppost.getParams().setParameter("fb_user_json", gson);
   		    	HttpResponse response = httpclient.execute(httppost);
   		    	StatusLine statusLine = response.getStatusLine();
   		    	int statusCode = statusLine.getStatusCode();
		    	if (statusCode == 200) {
		    		HttpEntity entity = response.getEntity();
		    		InputStream content = entity.getContent();
		    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    		String line;
		    		while ((line = reader.readLine()) != null) 
		    			builder.append(line);
		    		
		    	}
		    	else
		    		Log.e("err", "Failed to download file");
   		    }catch (ClientProtocolException e){
   		      e.printStackTrace();
   		    }catch (IOException e){
   		      e.printStackTrace();
   		    }
   		    
     	      ML.log(actname,"Response : "+builder.toString());
			return null;
			
		}
    }
	
	@Override
	public void onResume() {
	    super.onResume();
	    Session session = Session.getActiveSession();
	    if(!session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream")))
	    	shareFbCheckbox.setChecked(false);
	    
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onBackPressed() {
		finish();
    }
	
}