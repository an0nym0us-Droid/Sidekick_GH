package com.example.sidekick_offline_try2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

public class SplashScreen extends Activity {

	public String actname = "SplashScreen";
	public static String devicekey;
	ListDataSource datasource;
	SharedPreferences myPreferences;
	SharedPreferences.Editor editor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splashscreen);
	    
	    
	    
	    
	 // Add code to print out the key hash
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.example.sidekick_offline_try2", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	    //Initializations
	    myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    editor = myPreferences.edit();
	    devicekey = myPreferences.getString("devicekey", "");
	    editor.putBoolean("showprofile", true);
	    editor.commit();
	    datasource = new ListDataSource(this,getResources().getString(R.string.domain));
		datasource.createDatabase();
        datasource.open();
        
        
	    if(devicekey.equals("")){
	    	Log.e("Sidekick_V3", "Creating Device Key for the first time.");
	    	genDevKey();
	    }
	    else{
	    	Log.e("Sidekick_V3", "Using previous devicekey : "+devicekey);
	    }
	    
	    

	    Log.e("Sidekick_V3:","Transforming Tuneins.");
	    datasource.transformTuneins();
	    if(checkForRecommended()){
	    	
	    	new Recommended().execute(DateUtil.formatTime(System.currentTimeMillis()),devicekey,"recommend");
	    	new Recommended().execute(DateUtil.formatTime(System.currentTimeMillis()),devicekey,"watchlist");
	    	
	    }
		else
	    	ML.log(actname,"NOT RECOMMENDING");
	    /** New Handler to start the Menu-Activity 
         *  and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            	// Getting preferences.
               
            	
                // second argument is the default to use if the preference can't be found
                Boolean firsttime = myPreferences.getBoolean("firsttime", true);

                if (firsttime) {
                	Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), WalkthroughActivity.class);
                    startActivity(intent);
                }
                else{
                	Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 2000);
	    
	}
	
	public void genDevKey(){
		
		devicekey = getRandomNumber(16).toString();
		ML.log(actname, "New devicekey generated : "+devicekey);
		editor.putInt("dk", 1);
        editor.putString("devicekey", devicekey);
        editor.putString("deviceid", Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID));
        editor.putString("fbid", "");
        editor.putString("accesstoken", "");
        editor.putBoolean("askedpubperm", false);
        editor.commit(); 
	}
	public static BigInteger getRandomNumber(final int digCount){
	    return getRandomNumber(digCount, new Random());
	}
	public static BigInteger getRandomNumber(final int digCount, Random rnd){
	    final char[] ch = new char[digCount];
	    for(int i = 0; i < digCount; i++){
	        ch[i] =
	            (char) ('0' + (i == 0 ? rnd.nextInt(9) + 1 : rnd.nextInt(10)));
	    }
	    return new BigInteger(new String(ch));
	}
	
	
	
	

	public boolean checkForRecommended(){
    	String last_update = myPreferences.getString("recolastupdate", "0000-01-01 00:00:00");
    	Date date_lastupdated=null;
    	try {
    		date_lastupdated = DateUtil.formatTime(last_update);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	if(System.currentTimeMillis()-date_lastupdated.getTime()>1000)
    		return true;
    	else
    		return false;
    }
	
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	/* PROFILE BUILD UP	*/
	
	
	
	
	/* ************* RECOMMENDATIONS **********************/
	
	public class Recommended extends AsyncTask<String, Void, String>{
		String type;
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			 StringBuilder builder = new StringBuilder();
			    StringBuilder url = new StringBuilder();
			    type=params[2];
			    //Building the url using the parameters. Check trello Dev Spech for format.
			    url.append(getResources().getString(R.string.domain)+"re/"+params[2]+"?");
			    url.append("device_key="+params[1]);
			    url.append("&request_time="+params[0]);
			    String finURL = url.toString();
			    String sp = " ";
			    String per = "%20";
			    //Replacing spaces with %20. Spaces are characterised as invalid character in HTTP get query.
			    finURL = finURL.replaceAll("["+Pattern.quote(sp)+"]", per);
			   Log.i("Sidekick_V4","Getting : "+params[2] +": "+finURL);
			    HttpParams httpParameters = new BasicHttpParams();
			 // Set the timeout in milliseconds until a connection is established.
			 // The default value is zero, that means the timeout is not used. 
			 int timeoutConnection = 10000;
			 HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			 // Set the default socket timeout (SO_TIMEOUT) 
			 // in milliseconds which is the timeout for waiting for data.
			 int timeoutSocket = 10000;
			 HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		     HttpClient client = new DefaultHttpClient(httpParameters);
		     HttpGet httpGet = new HttpGet(finURL);
			 try{
			    	HttpResponse response = client.execute(httpGet);
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
			    		ML.log(actname, "Failed to download file");
			    }catch (ClientProtocolException e){
			      e.printStackTrace();
			    }catch(SocketTimeoutException e1){
			      System.out.println(e1);
			    }catch (IOException e){
			      e.printStackTrace();
			    }
			    
			    Log.e("Sidekick_V4","Result : "+params[2]+" --> "+builder.toString() );
			return builder.toString();
		}
		
		
		
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.i("Builder", result);
			getRecommendedChannels(result,type);
			editor.putString("recolastupdate", DateUtil.formatTime(System.currentTimeMillis()));
			editor.commit();
			if(type.equals("watchlist"))
				datasource.close();

		}
		
		
	}
	
	private void getRecommendedChannels(String result,String type){
		String readResponse = result;
		  int chid;
		  int listingIdReceived;
		  String stTimeReceived,endTimeReceived,titleReceived,timeslotReceived,progSynopsisReceived,epiSynopsisReceived,progIdReceived,imageURLReceived,retelecastReceived,youtubeReceived;
		  String image_url;
		    try {
		    	//Our JSON Object contains two fields namely 'Listings' and 'listings_metadata'
		    	JSONObject jj = new JSONObject(readResponse);
		    	//Here we get the Listings fields which itself is an array.
		    	JSONArray totalListings =jj.getJSONArray("Listings");
		    	//Now we get listings of each channel one by one using this loop.
		    	if(totalListings.length()==0){
		    		return;
		    	}
		    	for(int j=0;j<totalListings.length();j++){
		    		JSONObject jsonObject =totalListings.getJSONObject(j);	// jth channel taken from the listings.
		    		// Getting listings of the jth channel.
		    			chid = jsonObject.getInt("sidekick_id"); 
		    			stTimeReceived = jsonObject.getString("start_time");
		    			endTimeReceived = jsonObject.getString("end_time");
		    			timeslotReceived = jsonObject.getString("timeslot");
		    			retelecastReceived="";
		    			if(!jsonObject.isNull("retelecast")){
		    				if(jsonObject.getJSONArray("retelecast").length()!=0)
				    			retelecastReceived = jsonObject.getJSONArray("retelecast").getString(0);
				    				retelecastReceived = retelecastReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
					    			retelecastReceived = retelecastReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			}
		    			
		    			youtubeReceived="";
		    			if(!jsonObject.isNull("youtube_link"))
		    				youtubeReceived=jsonObject.getString("youtube_link");
		    			
		    			
		    			// Formatting start and end time. Removing T and Z from it
		    			stTimeReceived = stTimeReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
		    			stTimeReceived = stTimeReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			endTimeReceived = endTimeReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
		    			endTimeReceived = endTimeReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			timeslotReceived = timeslotReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
		    			timeslotReceived = timeslotReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			imageURLReceived = jsonObject.getString("image_url");
		    			titleReceived = jsonObject.getString("title");
		    			progSynopsisReceived = jsonObject.getString("synopsis");
		    			epiSynopsisReceived = jsonObject.getString("episode_synopsis");
		    			listingIdReceived = jsonObject.getInt("id");
		    			progIdReceived = jsonObject.getString("program_id");
		    			
		    			//Adding the above extracted details into our channels_listings table
		    			datasource.setListingasRecommended(listingIdReceived,chid, stTimeReceived, endTimeReceived, titleReceived, progSynopsisReceived,epiSynopsisReceived, imageURLReceived,progIdReceived,timeslotReceived,retelecastReceived,youtubeReceived,type);
		    			
		    	}
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	}
	/* ***********   END RECOMMENDATIONS *****************/
}
